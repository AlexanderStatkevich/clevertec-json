package ru.clevertec.jsonparser.service.deserialize;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Parser {

    private final static Character comma = ',';
    private final static Character leftFigureBracket = '{';
    private final static Character rightFigureBracket = '}';
    private final static Character leftStraightBracket = '[';
    private final static Character rightStraightBracket = ']';
    private final static Pattern rowPattern = Pattern.compile("\"(\\w+)\":\"?([\\w\\s]+)\"?,?");


    public static Map<String, String> parseTo(String jsonWithBrackets) {
        String json = jsonWithBrackets.substring(1);

        Map<String, String> parsedMap = new HashMap<>();
        final Deque<JsonNode> stack = new ArrayDeque<>();
        Map<String, Integer> arrayIndex = new HashMap<>();
        int previous = 0;

        for (int i = 0; i < json.length(); i++) {
            char symbol = json.charAt(i);
            //todo last element condition
            if (comma.equals(symbol)) {
                //todo explain i!=previous
                if (i != previous) {
                    String row = json.substring(previous, i + 1);
                    Map.Entry<String, String> entry = parseRow(row);
                    String prefix = getPrefix(stack);
                    String key = prefix.isEmpty()
                            ? entry.getKey()
                            : prefix + "." + entry.getKey();
                    parsedMap.put(key, entry.getValue());
                }
                previous = i + 1;
            }

            if (leftFigureBracket.equals(symbol)) {

                boolean isArray = !stack.isEmpty() && NodeType.ARRAY.equals(stack.getLast().getType());
                if (isArray) {
                    String arrayName = stack.getLast().getName();
                    Integer index = arrayIndex.get(arrayName);

                    if (index == null) {
                        index = 0;
                    } else {
                        index = index + 1;
                    }
                    arrayIndex.put(arrayName, index);
                    stack.addLast(new JsonNode("[" + index + "]", NodeType.OBJECT));
                } else {
                    String row = json.substring(previous, i);
                    String name = parseComponent(row);
                    stack.addLast(new JsonNode(name, NodeType.OBJECT));
                }
                previous = i + 1;
            }

            if (rightFigureBracket.equals(symbol)) {

                if (i != previous) {
                    String row = json.substring(previous, i);
                    Map.Entry<String, String> entry = parseRow(row);
                    String prefix = getPrefix(stack);
                    String key = prefix.isEmpty()
                            ? entry.getKey()
                            : prefix + "." + entry.getKey();
                    parsedMap.put(key, entry.getValue());
                }
                previous = i + 1;
                if (i != json.length() - 1) {
                    stack.removeLast();
                }
            }

            if (leftStraightBracket.equals(symbol)) {
                String row = json.substring(previous, i);
                String name = parseComponent(row);
                stack.addLast(new JsonNode(name, NodeType.ARRAY));

                previous = i + 1;
            }
            if (rightStraightBracket.equals(symbol)) {

                previous = i + 1;
                stack.removeLast();
            }
        }
        return parsedMap;
    }

    private static Map.Entry<String, String> parseRow(String row) {
        Matcher matcher = rowPattern.matcher(row);
        matcher.matches();
        String key = matcher.group(1);
        String value = matcher.group(2);
        return Map.entry(key, value);
    }

    private static String parseComponent(String row) {
        return row.substring(1, row.length() - 2);
    }

    private static String getPrefix(Deque<JsonNode> stack) {
        return stack.stream()
                .map(JsonNode::getName)
                .collect(Collectors.joining("."));
    }
}
