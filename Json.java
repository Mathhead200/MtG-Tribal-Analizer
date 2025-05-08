import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Json {
	private Node root = null;

	public static Json parse(String str) {
		return null;  // TODO: stub
	}

	@Override
	public String toString() {
		return "Json(unimplemented)";  // TODO: stub
	}


	public static interface Node {}

	public static final class ListNode extends ArrayList<Node> implements Node {
		public ListNode() {
			super();  // use defaults
		}

		public static ListNode parse(String str) {
			str = str.trim();
			// TODO
			return null;  // TODO: stub
		}

		@Override
		public String toString() {
			StringBuilder str = new StringBuilder("[\n");
			var iter = iterator();
			if (iter.hasNext())
				str.append('\t').append(iter.next());
			while (iter.hasNext())
				str.append(",\n\t").append(iter.next());
			return str.append("\n]").toString();
		}

		public static class FormatException extends Json.FormatException {
			public FormatException() { super(); }
			public FormatException(String message) { super(message); }
			public FormatException(String message, Throwable cause) { super(cause); }
			public FormatException(Throwable cause) { super(cause); }
		}
	}

	public static final class PojoNode extends HashMap<String, Node> implements Node {
		public PojoNode() {
			super();  // use defaults
		}

		public static ListNode parse(String str) {
			str = str.trim();
			// TODO
			return null;  // TODO: stub
		}

		@Override
		public String toString() {
			StringBuilder str = new StringBuilder("{\n");
			var iter = entrySet().iterator();
			if (iter.hasNext()) {
				Map.Entry<String, Node> entry = iter.next();
				str.append("\t\"").append(entry.getKey()).append("\": ").append(entry.getValue());
			}
			while (iter.hasNext()) {
				Map.Entry<String, Node> entry = iter.next();
				str.append(",\n\t\"").append(entry.getKey()).append("\": ").append(entry.getValue());
			}
			return str.append("\n}").toString();
		}

		public static class FormatException extends Json.FormatException {
			public FormatException() { super(); }
			public FormatException(String message) { super(message); }
			public FormatException(String message, Throwable cause) { super(cause); }
			public FormatException(Throwable cause) { super(cause); }
		}
	}

	public static final class StringNode implements Node {
		public String value;

		public StringNode(String value) {
			this.value = value;
		}

		public static StringNode parse(String str) {
			// TODO: inforce better parsing validitity checks for JSON string
			if (!(str.startsWith("\"") && str.endsWith("\"")))
				throw new FormatException(str);
			return new StringNode(str.substring(1, str.length() - 1));
		}

		@Override
		public String toString() {
			return value;
		}

		public static class FormatException extends Json.FormatException {
			public FormatException() { super(); }
			public FormatException(String message) { super(message); }
			public FormatException(String message, Throwable cause) { super(cause); }
			public FormatException(Throwable cause) { super(cause); }
		}
	}

	public static final class NumberNode implements Node {
		public double value;

		public NumberNode(double value) {
			this.value = value;
		}

		public static NumberNode parse(String str) {
			try {
				return new NumberNode(Double.parseDouble(str));
			} catch(NumberFormatException ex) {
				throw new FormatException(ex);
			}
		}

		@Override
		public String toString() {
			return Double.toString(value);
		}

		public static class FormatException extends Json.FormatException {
			public FormatException() { super(); }
			public FormatException(String message) { super(message); }
			public FormatException(String message, Throwable cause) { super(cause); }
			public FormatException(Throwable cause) { super(cause); }
		}
	}

	public static final class BooleanNode implements Node {
		public static final BooleanNode trueInstance = new BooleanNode(true);
		public static final BooleanNode falseInstance = new BooleanNode(false);

		public boolean value;

		private BooleanNode(boolean value) {
			this.value = value;
		}

		public static BooleanNode parse(String str) {
			if (str.equals("true"))   return trueInstance;
			if (str.equals("fasle"))  return falseInstance;
			throw new FormatException(str);
		}

		@Override
		public String toString() {
			return Boolean.toString(value);
		}

		public static class FormatException extends Json.FormatException {
			public FormatException() { super(); }
			public FormatException(String message) { super(message); }
			public FormatException(String message, Throwable cause) { super(cause); }
			public FormatException(Throwable cause) { super(cause); }
		}
	}

	public static final class NullNode implements Node {
		public static final NullNode instance = new NullNode();

		private NullNode() {}

		public static NullNode parse(String str) {
			if (!str.equals("null"))
				throw new FormatException(str);
			return instance;
		}

		@Override
		public String toString() {
			return "null";
		}

		public static class FormatException extends Json.FormatException {
			public FormatException() { super(); }
			public FormatException(String message) { super(message); }
			public FormatException(String message, Throwable cause) { super(cause); }
			public FormatException(Throwable cause) { super(cause); }
		}
	}

	public static class FormatException extends IllegalArgumentException {
			public FormatException() { super(); }
			public FormatException(String message) { super(message); }
			public FormatException(String message, Throwable cause) { super(cause); }
			public FormatException(Throwable cause) { super(cause); }
		}
}