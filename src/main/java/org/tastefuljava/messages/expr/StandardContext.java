package org.tastefuljava.messages.expr;

public class StandardContext extends CompilationContext {
    {
        defineConst("false", Boolean.FALSE);
        defineConst("true", Boolean.TRUE);
    }
}
