package org.nanocontainer.nanoweb.defaults;

import java.util.Map;

import ognl.MethodFailedException;
import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.nanocontainer.nanoweb.ExpressionEvaluator;
import org.nanocontainer.nanoweb.impl.OgnlNullHandle;
import org.nanocontainer.nanoweb.impl.OgnlTypeConverterAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.gems.ThreadLocalReference;

public class OgnlExpressionEvaluator implements ExpressionEvaluator {

    public static final String CTX_KEY_COLLECTION_OPERATION = "CTX_KEY_COLLECTION_OPERATION";

    public static final String COLLECTION_OPERATION_ADD = "COLLECTION_OPERATION_ADD";

    public static final String COLLECTION_OPERATION_REMOVE = "COLLECTION_OPERATION_REMOVE";

    private final transient ObjectReference picoReference = new ThreadLocalReference();

    private final transient OgnlNullHandle ognlNullHandle = new OgnlNullHandle(picoReference);

    public void set(PicoContainer pico, Object root, String expression, Object value) throws Exception {
        try {
            picoReference.set(pico);
            Map ctx = Ognl.createDefaultContext(root);
            OgnlRuntime.setNullHandler(Object.class, ognlNullHandle);
            Ognl.setTypeConverter(ctx, new OgnlTypeConverterAdapter(pico));

            if (expression.endsWith("+")) {
                ctx.put(CTX_KEY_COLLECTION_OPERATION, COLLECTION_OPERATION_ADD);
                Ognl.setValue(expression.substring(0, expression.length() - 1), ctx, root, value);
            } else if (expression.endsWith("-")) {
                ctx.put(CTX_KEY_COLLECTION_OPERATION, COLLECTION_OPERATION_REMOVE);
                Ognl.setValue(expression.substring(0, expression.length() - 1), ctx, root, value);
            } else {
                Ognl.setValue(expression, ctx, root, value);
            }
        } catch (NoSuchPropertyException e) {
            // Do nothing...
        } catch (MethodFailedException e) {
            // Do nothing...
        } catch (OgnlException e) {
            if (e.getReason() instanceof NumberFormatException) {
                // Do nothing...
            } else {
                throw e;
            }
        }
    }

}
