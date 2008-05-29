package org.picocontainer.script.groovy.buildernodes;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.script.ScriptedPicoContainerMarkupException;

import java.util.Map;
import java.util.Iterator;


/**
 * config node adds configuration entry to mutable pico container. 
 * it requires 2 named parameters: key ( shall ne string )  and value ( just object )
 * use it as: config(key:'foo',value:'bar')
 * @author k.pribluda
 *
 */
public class ConfigNode extends AbstractBuilderNode {

    public static final String NODE_NAME = "config";

    /**
     * attribute name for key attribute ( Required ) 
     */
    public static final String KEY="key";
    /**
     * attribute name for value attribute ( Required )  
     */
    public static final String  VALUE="value";
    public ConfigNode() {
        super(NODE_NAME);
    }

    public Object createNewNode(Object current, Map attributes) {
    	validateScriptedAttributes(attributes);
        ((MutablePicoContainer) current).addConfig((String) attributes.get(KEY),  attributes.get(VALUE));
        return null;
    }

    /**
     * ansure that node has proper attributes
     */
    public void validateScriptedAttributes(Map specifiedAttributes) throws ScriptedPicoContainerMarkupException {
        if (specifiedAttributes.size() != 2 || !isAttribute(specifiedAttributes, KEY) || !isAttribute(specifiedAttributes,VALUE)) {
            throw new ScriptedPicoContainerMarkupException("config has two parameters - key and value");
        }
    }
}