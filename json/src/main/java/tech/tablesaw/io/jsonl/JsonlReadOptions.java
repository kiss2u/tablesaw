package tech.tablesaw.io.jsonl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.io.Source;

public class JsonlReadOptions extends ReadOptions {

  private final String path;

  protected JsonlReadOptions(Builder builder) {
    super(builder);
    this.path = builder.path;
  }

  public static Builder builder(Source source) {
    return new Builder(source);
  }

  public static Builder builder(File file) {
    return new Builder(file).tableName(file.getName());
  }

  public static Builder builder(String fileName) {
    return new Builder(new File(fileName));
  }

  public static Builder builder(URL url) throws IOException {
    return new Builder(url);
  }

  public static Builder builderFromFile(String fileName) {
    return new Builder(new File(fileName));
  }

  public static Builder builderFromString(String contents) {
    return new Builder(new StringReader(contents));
  }

  public static Builder builderFromUrl(String url) throws IOException {
    return new Builder(new URL(url));
  }

  public static Builder builder(InputStream stream) {
    return new Builder(stream);
  }

  public static Builder builder(Reader reader) {
    return new Builder(reader);
  }

  public String path() {
    return path;
  }

  public static class Builder extends ReadOptions.Builder {

    private String path;

    protected Builder(Source source) {
      super(source);
    }

    protected Builder(URL url) throws IOException {
      super(url);
    }

    public Builder(File file) {
      super(file);
    }

    protected Builder(Reader reader) {
      super(reader);
    }

    protected Builder(InputStream stream) {
      super(stream);
    }

    @Override
    public JsonlReadOptions build() {
      return new JsonlReadOptions(this);
    }

    // Override super-class setters to return an instance of this class

    @Override
    public Builder header(boolean header) {
      super.header(header);
      return this;
    }

    @Override
    public Builder tableName(String tableName) {
      super.tableName(tableName);
      return this;
    }

    @Override
    public Builder sample(boolean sample) {
      super.sample(sample);
      return this;
    }

    @Override
    public Builder dateFormat(DateTimeFormatter dateFormat) {
      super.dateFormat(dateFormat);
      return this;
    }

    @Override
    public Builder timeFormat(DateTimeFormatter timeFormat) {
      super.timeFormat(timeFormat);
      return this;
    }

    @Override
    public Builder dateTimeFormat(DateTimeFormatter dateTimeFormat) {
      super.dateTimeFormat(dateTimeFormat);
      return this;
    }

    @Override
    public Builder locale(Locale locale) {
      super.locale(locale);
      return this;
    }

    @Override
    public Builder missingValueIndicator(String... missingValueIndicators) {
      super.missingValueIndicator(missingValueIndicators);
      return this;
    }

    @Override
    public Builder minimizeColumnSizes() {
      super.minimizeColumnSizes();
      return this;
    }

    /**
     * @param path the JSON Pointer path used to select a sub-tree in the main document
     */
    public Builder path(String path) {
      this.path = path;
      return this;
    }

    @Override
    public Builder columnTypes(ColumnType[] columnTypes) {
      super.columnTypes(columnTypes);
      return this;
    }

    @Override
    public Builder columnTypes(Function<String, ColumnType> columnTypeFunction) {
      super.columnTypes(columnTypeFunction);
      return this;
    }

    @Override
    public Builder columnTypesPartial(Function<String, Optional<ColumnType>> columnTypeFunction) {
      super.columnTypesPartial(columnTypeFunction);
      return this;
    }

    @Override
    public Builder columnTypesPartial(Map<String, ColumnType> columnTypeByName) {
      super.columnTypesPartial(columnTypeByName);
      return this;
    }
  }
}
