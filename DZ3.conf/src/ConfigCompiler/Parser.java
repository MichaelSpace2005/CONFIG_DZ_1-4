package ConfigCompiler;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private String input;
    private List<Token> tokens;
    private int currentTokenIndex;
    public Map<String, Object> constants;

    public Parser(String input) {
        this.input = input;
        this.tokens = new ArrayList<>();
        this.currentTokenIndex = 0;
        this.constants = new HashMap<>();
        tokenize();
    }

    private void tokenize() {
        String regex = "\\s*(" +
                "(\\()|(\\))|(@\\{)|(\\})|(=)|(;)|(,)|" +
                "('([^']*)')|" +        // Single-quoted strings
                "(\"([^\"]*)\")|" +     // Double-quoted strings
                "([0-9]+)|" +           // Numbers
                "([_A-Z][_a-zA-Z0-9]*)|" + // Names (identifiers)
                "(const)|(!\\[)|(\\+)|(-)|(\\*)|(/)|(pow\\(\\))|(sort\\(\\))" +
                ")\\s*";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (matcher.group(2) != null) {
                tokens.add(new Token(TokenType.LEFT_PAREN, "("));
                System.out.println("Token: LEFT_PAREN, (" );
            } else if (matcher.group(3) != null) {
                tokens.add(new Token(TokenType.RIGHT_PAREN, ")"));
                System.out.println("Token: RIGHT_PAREN, )" );
            } else if (matcher.group(4) != null) {
                tokens.add(new Token(TokenType.LEFT_BRACE, "@{"));
                System.out.println("Token: LEFT_BRACE, @{" );
            } else if (matcher.group(5) != null) {
                tokens.add(new Token(TokenType.RIGHT_BRACE, "}"));
                System.out.println("Token: RIGHT_BRACE, }" );
            } else if (matcher.group(6) != null) {
                tokens.add(new Token(TokenType.EQUAL, "="));
                System.out.println("Token: EQUAL, =" );
            } else if (matcher.group(7) != null) {
                tokens.add(new Token(TokenType.SEMICOLON, ";"));
                System.out.println("Token: SEMICOLON, ;" );
            } else if (matcher.group(8) != null) {
                tokens.add(new Token(TokenType.COMMA, ","));
                System.out.println("Token: COMMA, ," );
            } else if (matcher.group(9) != null) { // Single-quoted string
                tokens.add(new Token(TokenType.STRING, matcher.group(10)));
                System.out.println("Token: STRING (single-quoted), " + matcher.group(10));
            } else if (matcher.group(11) != null) { // Double-quoted string
                tokens.add(new Token(TokenType.STRING, matcher.group(12)));
                System.out.println("Token: STRING (double-quoted), " + matcher.group(12));
            } else if (matcher.group(13) != null) { // Number
                tokens.add(new Token(TokenType.NUMBER, matcher.group(13)));
                System.out.println("Token: NUMBER, " + matcher.group(13));
            } else if (matcher.group(14) != null) { // Name (identifier)
                tokens.add(new Token(TokenType.NAME, matcher.group(14)));
                System.out.println("Token: NAME, " + matcher.group(14));
            } else if (matcher.group(15) != null) { // const
                tokens.add(new Token(TokenType.CONST, "const"));
                System.out.println("Token: CONST, const");
            } else if (matcher.group(16) != null) { // ![
                tokens.add(new Token(TokenType.BANG, "!["));
                System.out.println("Token: BANG, ![");
            } else if (matcher.group(17) != null) { // +
                tokens.add(new Token(TokenType.PLUS, "+"));
                System.out.println("Token: PLUS, +");
            } else if (matcher.group(18) != null) { // -
                tokens.add(new Token(TokenType.MINUS, "-"));
                System.out.println("Token: MINUS, -");
            } else if (matcher.group(19) != null) { // *
                tokens.add(new Token(TokenType.STAR, "*"));
                System.out.println("Token: STAR, *");
            } else if (matcher.group(20) != null) { // /
                tokens.add(new Token(TokenType.SLASH, "/"));
                System.out.println("Token: SLASH, /");
            } else if (matcher.group(21) != null) { // pow()
                tokens.add(new Token(TokenType.POW, "pow"));
                System.out.println("Token: POW, pow");
            } else if (matcher.group(22) != null) { // sort()
                tokens.add(new Token(TokenType.SORT, "sort"));
                System.out.println("Token: SORT, sort");
            }

            for (int i = 0; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    System.out.println("  Group " + i + ": " + matcher.group(i));
                }
            }
        }
        tokens.add(new Token(TokenType.EOF, null));
        System.out.println("Token: EOF");
    }
    public List<Object> parseStatements() {
        List<Object> statements = new ArrayList<>();
        while (currentToken().getType() != TokenType.EOF) {
            if (currentToken().getType() == TokenType.CONST) {
                statements.add(parseConstDeclaration());
            } else {
                statements.add(parseValue());
                match(TokenType.SEMICOLON);
            }

        }
        return statements;
    }

    private Object parseConstDeclaration() {
        match(TokenType.CONST);
        String name = match(TokenType.NAME).getValue();
        match(TokenType.EQUAL);
        Object value = parseValue();
        match(TokenType.SEMICOLON);
        constants.put(name, value);
        return null;
    }


    private Object parseValue() {
        Token token = currentToken();
        switch (token.getType()) {
            case NUMBER:
                match(TokenType.NUMBER);
                return Integer.parseInt(token.getValue());
            case STRING:
                match(TokenType.STRING);
                return token.getValue();
            case NAME:
                match(TokenType.NAME);
                if (constants.containsKey(token.getValue())) {
                    return constants.get(token.getValue());
                } else {
                    throw new RuntimeException("Undefined constant: " + token.getValue());
                }
            case LEFT_BRACE:
                return parseDictionary();
            case BANG:
                return parseArray();
            case LEFT_PAREN:
                match(TokenType.LEFT_PAREN);
                Object expr = evaluateExpression();
                match(TokenType.RIGHT_PAREN);
                return expr;
            default:
                throw new RuntimeException("Unexpected token: " + token.getType());
            }
        }

    private List<Object> parseArray() {
        match(TokenType.BANG);
        match(TokenType.LEFT_PAREN);
        List<Object> elements = new ArrayList<>();
        if (currentToken().getType() != TokenType.RIGHT_PAREN) {
            elements.add(parseValue());
            while (currentToken().getType() == TokenType.COMMA) {
                match(TokenType.COMMA);
                elements.add(parseValue());
            }
        }
        match(TokenType.RIGHT_PAREN);
        return elements;
    }

    private Map<String, Object> parseDictionary() {
        match(TokenType.LEFT_BRACE);
        Map<String, Object> entries = new LinkedHashMap<>();
        while (currentToken().getType() != TokenType.RIGHT_BRACE) {
            String key = match(TokenType.STRING).getValue();
            match(TokenType.COMMA);
            Object value = parseValue();
            entries.put(key, value);

            if (currentToken().getType() != TokenType.RIGHT_BRACE) {
                match(TokenType.COMMA);
            }
        }

        match(TokenType.RIGHT_BRACE);
        return entries;
    }


    private Object evaluateExpression() {
        Object left = parseValue();
        while (currentToken().getType() == TokenType.PLUS || currentToken().getType() == TokenType.MINUS ||
                currentToken().getType() == TokenType.STAR || currentToken().getType() == TokenType.SLASH ||
                currentToken().getType() == TokenType.POW || currentToken().getType() == TokenType.SORT) {
            Token operator = currentToken();
            match(operator.getType()); // Consume the operator
            Object right = parseValue(); // Parse the next value which is the right operand
            if (left instanceof Integer && right instanceof Integer) {
                int leftInt = (int) left;
                int rightInt = (int) right;
                switch (operator.getType()) {
                    case PLUS:
                        left = leftInt + rightInt;
                        break;
                    case MINUS:
                        left = leftInt - rightInt;
                        break;
                    case STAR:
                        left = leftInt * rightInt;
                        break;
                    case SLASH:
                        left = leftInt / rightInt;
                        break;
                    default:
                        throw new RuntimeException("Operator not supported yet!");
                }
            } else {
                throw new RuntimeException("Cannot perform operation on non-integer values.");
            }
        }
        return left;
    }
    private Token currentToken() {
        return tokens.get(currentTokenIndex);
    }

    private Token match(TokenType expected) {
        Token token = currentToken();
        if (token.getType() == expected) {
            currentTokenIndex++;
            return token;
        } else {
            throw new RuntimeException("Expected token " + expected + ", but found " + token.getType());
        }
    }
}

//evaluateExpression