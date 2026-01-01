package at.fhj.softsec.baba.cli;

import java.util.*;

public class CommandRegistry {

    private final Map<StringArrayKey, Command> commands = new LinkedHashMap<>();

    public void register(Command command) {
        commands.put(new StringArrayKey(command.name()), command);
    }

    public Optional<Command> findCommand(String[] args) {
        List<String> key = new ArrayList<>();

        for (String arg : args) {
            key.add(arg);
            Command command = commands.get(new StringArrayKey(key.toArray(new String[0])));
            if (command != null) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }


    public Collection<Command> getAllCommands() {
        return commands.values();
    }

    private class StringArrayKey implements Comparable<StringArrayKey> {

        private final String[] value;

        public StringArrayKey(String[] value) {
            this.value = value.clone(); // defensive copy
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof StringArrayKey other &&
                    Arrays.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(value);
        }

        @Override
        public int compareTo(StringArrayKey stringArrayKey) {
            return Arrays.compare(value, stringArrayKey.value);
        }
    }
}
