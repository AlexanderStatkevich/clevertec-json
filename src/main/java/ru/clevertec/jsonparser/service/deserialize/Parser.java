package ru.clevertec.jsonparser.service.deserialize;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Parser {
    private final static char COMMA = ',';
    private final static char LEFT_FIGURE_BRACKET = '{';
    private final static char RIGHT_FIGURE_BRACKET = '}';
    private final static char LEFT_STRAIGHT_BRACKET = '[';
    private final static char RIGHT_STRAIGHT_BRACKET = ']';

    private final static List<Character> SYMBOLS = List.of(COMMA, LEFT_FIGURE_BRACKET, RIGHT_FIGURE_BRACKET, LEFT_STRAIGHT_BRACKET, RIGHT_STRAIGHT_BRACKET);
    private final static Pattern ROW_PATTERN = Pattern.compile("\"(\\w+)\":\"?([\\w\\s]+)\"?,?");


    public static Map<String, String> parseJsonIntoMap(String jsonWithBrackets) {
        String json = jsonWithBrackets.substring(1);
        Map<String, String> parsedMap = new HashMap<>();
        final Deque<JsonNode> stack = new ArrayDeque<>();
        Map<String, Integer> arrayIndex = new HashMap<>();
        int previous = 0;

        for (int i = 0; i < json.length(); i++) {
            char symbol = json.charAt(i);
            if (SYMBOLS.contains(symbol)) {
                switch (symbol) {
                    case COMMA -> onComma(json, parsedMap, stack, previous, i);
                    case LEFT_FIGURE_BRACKET -> onLeftFigureBracket(json, stack, arrayIndex, previous, i);
                    case RIGHT_FIGURE_BRACKET -> onRightFigureBracket(json, parsedMap, stack, previous, i);
                    case LEFT_STRAIGHT_BRACKET -> onLeftStraightBracket(json, stack, previous, i);
                    case RIGHT_STRAIGHT_BRACKET -> onRightStraightBracket(stack);
                }
                previous = i + 1;
            }
        }
        return parsedMap;
    }

    private static void onRightStraightBracket(Deque<JsonNode> stack) {
        stack.removeLast();
    }

    private static void onLeftStraightBracket(String json, Deque<JsonNode> stack, int previous, int i) {
        String row = json.substring(previous, i);
        String name = parseComponent(row);
        stack.addLast(new JsonNode(name, NodeType.ARRAY));
    }

    private static void onRightFigureBracket(String json, Map<String, String> parsedMap, Deque<JsonNode> stack, int previous, int i) {
        if (i != previous) {
            String row = json.substring(previous, i);
            Map.Entry<String, String> entry = parseRow(row);
            String prefix = getPrefix(stack);
            String key = prefix.isEmpty()
                    ? entry.getKey()
                    : prefix + "." + entry.getKey();
            parsedMap.put(key, entry.getValue());
        }
        if (i != json.length() - 1) {
            stack.removeLast();
        }
    }

    private static void onLeftFigureBracket(String json, Deque<JsonNode> stack, Map<String, Integer> arrayIndex, int previous, int i) {
        boolean isArray = !stack.isEmpty() && NodeType.ARRAY.equals(stack.getLast().type());
        if (isArray) {
            String arrayName = stack.getLast().name();
            Integer currentIndex = arrayIndex.get(arrayName);
            Integer newIndex = currentIndex == null
                    ? 0
                    : currentIndex + 1;
            arrayIndex.put(arrayName, newIndex);
            stack.addLast(new JsonNode("[" + newIndex + "]", NodeType.OBJECT));
        } else {
            String row = json.substring(previous, i);
            String name = parseComponent(row);
            stack.addLast(new JsonNode(name, NodeType.OBJECT));
        }
    }

    private static void onComma(String json, Map<String, String> parsedMap, Deque<JsonNode> stack, int previous, int i) {
        // Condition (i != previous) help to avoid parsing row when comma separate objects in array
        if (i != previous) {
            String row = json.substring(previous, i + 1);
            Map.Entry<String, String> entry = parseRow(row);
            String prefix = getPrefix(stack);
            String key = prefix.isEmpty()
                    ? entry.getKey()
                    : prefix + "." + entry.getKey();
            parsedMap.put(key, entry.getValue());
        }
    }

    private static Map.Entry<String, String> parseRow(String row) {
        Matcher matcher = ROW_PATTERN.matcher(row);
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
                .map(JsonNode::name)
                .collect(Collectors.joining("."));
    }
}
