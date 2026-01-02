package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.exception.InputParseException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class Options {

    public static Builder builder() {
        return new Builder();
    }

    public final static class Builder {
        List<Option> options = new ArrayList<>();

        private Builder() {
        }

        public Builder withString(String name, Validator validator) {
            addOption(name, validator);
            return this;
        }

        public Builder withLong(String name) {
            addOption(name, InputParser::parseLong);
            return this;
        }

        public Builder withBigDecimal(String name) {
            addOption(name, InputParser::parseBigDecimal);
            return this;
        }

        private void addOption(String name, Validator validator) {
            options.add(new Option(name, validator));
        }

        public Options build() {
            return new Options(options);
        }

    }

    private final List<Option> options;
    private Map<String, Object> values;


    private Options(List<Option> options) {
        this.options = options;
    }

    public void parse(String[] args) throws InputParseException {
        values = new HashMap<>();
        for (int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            if (args.length <= i) {
                throw new InputParseException(format("Missing option <%s>", option.name));
            }
            values.put(option.name(), option.validator().validate(args[i]));
        }
    }

    public String getString(String name) {
        return (String) values.get(name);
    }

    public Long getLong(String name) {
        return (Long) values.get(name);
    }

    public BigDecimal getBigDecimal(String name) {
        return (BigDecimal) values.get(name);
    }

    public String getUsage() {
        return options.stream()
                .map(o -> format("<%s>", o.name))
                .collect(Collectors.joining(" "));
    }


    private record Option(String name, Validator validator) {
    }

    public interface Validator {
        Object validate(String value) throws InputParseException;
    }

}
