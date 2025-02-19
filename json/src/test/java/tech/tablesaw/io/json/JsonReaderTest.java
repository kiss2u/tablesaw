/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.io.json;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.tablesaw.api.ColumnType.DOUBLE;
import static tech.tablesaw.api.ColumnType.INSTANT;
import static tech.tablesaw.api.ColumnType.STRING;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Source;

public class JsonReaderTest {

  @Test
  public void arrayOfArraysWithHeader() {
    String json =
        "[[\"Date\",\"Value\"],[1453438800000,-2.1448117025014],[1454043600000,-2.9763153817574],[1454648400000,-2.9545283436391]]";
    Table table = Table.read().string(json, "json");
    assertEquals(2, table.columnCount());
    assertEquals(3, table.rowCount());
    assertEquals("Date", table.column(0).name());
    assertEquals("Value", table.column(1).name());
    assertEquals(ColumnType.LONG, table.typeArray()[0]);
    assertEquals(1453438800000L, table.column("Date").get(0));
  }

  @Test
  public void arrayOfArraysNoHeader() {
    String json =
        "[[1453438800000,-2.1448117025014],[1454043600000,-2.9763153817574],[1454648400000,-2.9545283436391]]";
    Table table = Table.read().string(json, "json");
    assertEquals(2, table.columnCount());
    assertEquals(3, table.rowCount());
    assertEquals(ColumnType.LONG, table.typeArray()[0]);
  }

  @Test
  public void arrayOfNestedObjects() {
    String json =
        "[{\"a\":1453438800000,\"b\":{\"c\":-2.1448117025014}},{\"a\":1454043600000,\"b\":{\"c\":-2.9763153817574}},{\"a\":1454648400000,\"b\":{\"c\":-2.9545283436391}}]";
    Table table = Table.read().string(json, "json");
    assertEquals(2, table.columnCount());
    assertEquals(3, table.rowCount());
    assertEquals("a", table.column(0).name());
    assertEquals("b.c", table.column(1).name());
    assertEquals(ColumnType.LONG, table.typeArray()[0]);
  }

  @Test
  public void arrayOfRowsWithIncompleteIndexes() {
    String json =
        "[" + "{\"A\" : \"123\", \"B\" : \"456\"}," + "{\"B\" : \"789\", \"C\" : \"123\"}" + "]";

    Table expected =
        Table.create(
            IntColumn.create("A", new int[] {123, Integer.MIN_VALUE}),
            IntColumn.create("B", new int[] {456, 789}),
            IntColumn.create("C", new int[] {Integer.MIN_VALUE, 123}));
    Table actual = Table.read().string(json, "json");

    assertEquals(ColumnType.INTEGER, actual.typeArray()[0]);
    assertEquals(expected.column("A").asList(), actual.column("A").asList());
    assertEquals(expected.column("B").asList(), actual.column("B").asList());
    assertEquals(expected.column("C").asList(), actual.column("C").asList());
  }

  @Test
  public void testCustomizedColumnTypesMixedWithDetection() throws IOException {
    String json =
        "[[\"Date\",\"Value\"],[\"2007-12-03T10:15:30.00Z\",-2.1448117025014],[\"2020-12-03T10:15:30.00Z\",-2.9763153817574],[\"2021-12-03T10:15:30.00Z\",-2.9545283436391]]";

    ColumnType[] columnTypes =
        new JsonReader()
            .read(
                JsonReadOptions.builderFromString(json)
                    .columnTypesPartial(ImmutableMap.of("Date", INSTANT))
                    .build())
            .typeArray();

    assertArrayEquals(columnTypes, new ColumnType[] {INSTANT, DOUBLE});
  }

  @Test
  public void testCustomizedColumnTypeAllCustomized() throws IOException {
    String json =
        "[[\"Date\",\"Value\"],[\"2007-12-03T10:15:30.00Z\",-2.1448117025014],[\"2020-12-03T10:15:30.00Z\",-2.9763153817574],[\"2021-12-03T10:15:30.00Z\",-2.9545283436391]]";

    ColumnType[] columnTypes =
        new JsonReader()
            .read(JsonReadOptions.builderFromString(json).columnTypes(columnName -> STRING).build())
            .typeArray();

    assertArrayEquals(columnTypes, new ColumnType[] {STRING, STRING});
  }

  @Test
  void testColumnOrderIsPreserved() {
    String json =
        "[{\"日期\":\"1991-04-03\",\"开盘\":49,\"收盘\":49,\"最高\":49,\"最低\":49,\"成交量\":1,\"成交额\":5000,\"振幅\":0,\"涨跌幅\":22.5,\"涨跌额\":9,\"换手率\":0},"
            + "{\"日期\":\"1991-04-04\",\"开盘\":48.76,\"收盘\":48.76,\"最高\":48.76,\"最低\":48.76,\"成交量\":3,\"成交额\":15000,\"振幅\":0,\"涨跌幅\":-0.49,\"涨跌额\":-0.24,\"换手率\":0}]\n";

    List<String> header =
        Arrays.asList("日期", "开盘", "收盘", "最高", "最低", "成交量", "成交额", "振幅", "涨跌幅", "涨跌额", "换手率");

    Table table = new JsonReader().read(Source.fromString(json));

    assertEquals(header, table.columnNames());
  }
}
