/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.deplant.java4ever.binding.generator.javapoet;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

import static tech.deplant.java4ever.binding.generator.javapoet.Util.*;

/**
 * A generated annotation on a declaration.
 */
public final class AnnotationSpec {
	public static final String VALUE = "value";

	public final TypeName type;
	public final Map<String, List<CodeBlock>> members;

	private AnnotationSpec(Builder builder) {
		this.type = builder.type;
		this.members = Util.immutableMultimap(builder.members);
	}

	public static AnnotationSpec get(Annotation annotation) {
		return get(annotation, false);
	}

	public static AnnotationSpec get(Annotation annotation, boolean includeDefaultValues) {
		Builder builder = builder(annotation.annotationType());
		try {
			Method[] methods = annotation.annotationType().getDeclaredMethods();
			Arrays.sort(methods, Comparator.comparing(Method::getName));
			for (Method method : methods) {
				Object value = method.invoke(annotation);
				if (!includeDefaultValues) {
					if (Objects.deepEquals(value, method.getDefaultValue())) {
						continue;
					}
				}
				if (value.getClass().isArray()) {
					for (int i = 0; i < Array.getLength(value); i++) {
						builder.addMemberForValue(method.getName(), Array.get(value, i));
					}
					continue;
				}
				if (value instanceof Annotation) {
					builder.addMember(method.getName(), "$L", get((Annotation) value));
					continue;
				}
				builder.addMemberForValue(method.getName(), value);
			}
		} catch (Exception e) {
			throw new RuntimeException("Reflecting " + annotation + " failed!", e);
		}
		return builder.build();
	}

	public static AnnotationSpec get(AnnotationMirror annotation) {
		TypeElement element = (TypeElement) annotation.getAnnotationType().asElement();
		AnnotationSpec.Builder builder = AnnotationSpec.builder(ClassName.get(element));
		Visitor visitor = new Visitor(builder);
		for (ExecutableElement executableElement : annotation.getElementValues().keySet()) {
			String name = executableElement.getSimpleName().toString();
			AnnotationValue value = annotation.getElementValues().get(executableElement);
			value.accept(visitor, name);
		}
		return builder.build();
	}

	public static Builder builder(ClassName type) {
		checkNotNull(type, "type == null");
		return new Builder(type);
	}

	public static Builder builder(Class<?> type) {
		return builder(ClassName.get(type));
	}

	void emit(CodeWriter codeWriter, boolean inline) throws IOException {
		String whitespace = inline ? "" : "\n";
		String memberSeparator = inline ? ", " : ",\n";
		if (this.members.isEmpty()) {
			// @Singleton
			codeWriter.emit("@$T", this.type);
		} else if (this.members.size() == 1 && this.members.containsKey("value")) {
			// @Named("foo")
			codeWriter.emit("@$T(", this.type);
			emitAnnotationValues(codeWriter, whitespace, memberSeparator, this.members.get("value"));
			codeWriter.emit(")");
		} else {
			// Inline:
			//   @Column(parameterName = "updated_at", nullable = false)
			//
			// Not inline:
			//   @Column(
			//       parameterName = "updated_at",
			//       nullable = false
			//   )
			codeWriter.emit("@$T(" + whitespace, this.type);
			codeWriter.indent(2);
			for (Iterator<Map.Entry<String, List<CodeBlock>>> i
			     = this.members.entrySet().iterator(); i.hasNext(); ) {
				Map.Entry<String, List<CodeBlock>> entry = i.next();
				codeWriter.emit("$L = ", entry.getKey());
				emitAnnotationValues(codeWriter, whitespace, memberSeparator, entry.getValue());
				if (i.hasNext()) {
					codeWriter.emit(memberSeparator);
				}
			}
			codeWriter.unindent(2);
			codeWriter.emit(whitespace + ")");
		}
	}

	private void emitAnnotationValues(CodeWriter codeWriter, String whitespace,
	                                  String memberSeparator, List<CodeBlock> values) throws IOException {
		if (values.size() == 1) {
			codeWriter.indent(2);
			codeWriter.emit(values.get(0));
			codeWriter.unindent(2);
			return;
		}

		codeWriter.emit("{" + whitespace);
		codeWriter.indent(2);
		boolean first = true;
		for (CodeBlock codeBlock : values) {
			if (!first) {
				codeWriter.emit(memberSeparator);
			}
			codeWriter.emit(codeBlock);
			first = false;
		}
		codeWriter.unindent(2);
		codeWriter.emit(whitespace + "}");
	}

	public Builder toBuilder() {
		Builder builder = new Builder(this.type);
		for (Map.Entry<String, List<CodeBlock>> entry : this.members.entrySet()) {
			builder.members.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
		return builder;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		return toString().equals(o.toString());
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		try {
			CodeWriter codeWriter = new CodeWriter(out);
			codeWriter.emit("$L", this);
			return out.toString();
		} catch (IOException e) {
			throw new AssertionError();
		}
	}

	public static final class Builder {
		public final Map<String, List<CodeBlock>> members = new LinkedHashMap<>();
		private final TypeName type;

		private Builder(TypeName type) {
			this.type = type;
		}

		public Builder addMember(String name, String format, Object... args) {
			return addMember(name, CodeBlock.of(format, args));
		}

		public Builder addMember(String name, CodeBlock codeBlock) {
			List<CodeBlock> values = this.members.computeIfAbsent(name, k -> new ArrayList<>());
			values.add(codeBlock);
			return this;
		}

		/**
		 * Delegates to {@link #addMember(String, String, Object...)}, with parameter {@code format}
		 * depending on the given {@code value} object. Falls back to {@code "$L"} literal format if
		 * the class of the given {@code value} object is not supported.
		 */
		Builder addMemberForValue(String memberName, Object value) {
			checkNotNull(memberName, "memberName == null");
			checkNotNull(value, "value == null, constant non-null value expected for %s", memberName);
			checkArgument(SourceVersion.isName(memberName), "not a valid parameterName: %s", memberName);
			if (value instanceof Class<?>) {
				return addMember(memberName, "$T.class", value);
			}
			if (value instanceof Enum) {
				return addMember(memberName, "$T.$L", value.getClass(), ((Enum<?>) value).name());
			}
			if (value instanceof String) {
				return addMember(memberName, "$S", value);
			}
			if (value instanceof Float) {
				return addMember(memberName, "$Lf", value);
			}
			if (value instanceof Character) {
				return addMember(memberName, "'$L'", characterLiteralWithoutSingleQuotes((char) value));
			}
			return addMember(memberName, "$L", value);
		}

		public AnnotationSpec build() {
			for (String name : this.members.keySet()) {
				checkNotNull(name, "parameterName == null");
				checkArgument(SourceVersion.isName(name), "not a valid parameterName: %s", name);
			}
			return new AnnotationSpec(this);
		}
	}

	/**
	 * Annotation value visitor adding members to the given builder instance.
	 */
	private static class Visitor extends SimpleAnnotationValueVisitor8<Builder, String> {
		final Builder builder;

		Visitor(Builder builder) {
			super(builder);
			this.builder = builder;
		}

		@Override
		protected Builder defaultAction(Object o, String name) {
			return this.builder.addMemberForValue(name, o);
		}

		@Override
		public Builder visitType(TypeMirror t, String name) {
			return this.builder.addMember(name, "$T.class", t);
		}

		@Override
		public Builder visitEnumConstant(VariableElement c, String name) {
			return this.builder.addMember(name, "$T.$L", c.asType(), c.getSimpleName());
		}

		@Override
		public Builder visitAnnotation(AnnotationMirror a, String name) {
			return this.builder.addMember(name, "$L", AnnotationSpec.get(a));
		}

		@Override
		public Builder visitArray(List<? extends AnnotationValue> values, String name) {
			for (AnnotationValue value : values) {
				value.accept(this, name);
			}
			return this.builder;
		}
	}
}
