package org.yaml.snakeyaml.representer;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.*;

import java.util.*;

/**
 * @description: 由于升级snakeyaml-2.0，导致shardingjdbc启动报错，
 *               原因是shardingjdbc调用snakeyaml的无参构造器，但是snakeyaml-2.0去掉了无参构造器
 *               所以重写snakeyaml-2.0的Representer类
 * @createDate: 2024/5/14 下午6:00
 **/
public class Representer extends SafeRepresenter {
    protected Map<Class<? extends Object>, TypeDescription> typeDefinitions = Collections.emptyMap();

    /**
     * 新增一个无参构造器
     */
    public Representer() {
        super(new DumperOptions());
        this.representers.put(null, new org.yaml.snakeyaml.representer.Representer.RepresentJavaBean());
    }

    public Representer(DumperOptions options) {
        super(options);
        this.representers.put(null, new org.yaml.snakeyaml.representer.Representer.RepresentJavaBean());
    }

    public TypeDescription addTypeDescription(TypeDescription td) {
        if (Collections.EMPTY_MAP == this.typeDefinitions) {
            this.typeDefinitions = new HashMap();
        }

        if (td.getTag() != null) {
            this.addClassTag(td.getType(), td.getTag());
        }

        td.setPropertyUtils(this.getPropertyUtils());
        return (TypeDescription)this.typeDefinitions.put(td.getType(), td);
    }

    public void setPropertyUtils(PropertyUtils propertyUtils) {
        super.setPropertyUtils(propertyUtils);
        Collection<TypeDescription> tds = this.typeDefinitions.values();
        Iterator var3 = tds.iterator();

        while(var3.hasNext()) {
            TypeDescription typeDescription = (TypeDescription)var3.next();
            typeDescription.setPropertyUtils(propertyUtils);
        }

    }

    protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
        List<NodeTuple> value = new ArrayList(properties.size());
        Tag customTag = (Tag)this.classTags.get(javaBean.getClass());
        Tag tag = customTag != null ? customTag : new Tag(javaBean.getClass());
        MappingNode node = new MappingNode(tag, value, DumperOptions.FlowStyle.AUTO);
        this.representedObjects.put(javaBean, node);
        DumperOptions.FlowStyle bestStyle = DumperOptions.FlowStyle.FLOW;
        Iterator var8 = properties.iterator();

        while(true) {
            NodeTuple tuple;
            do {
                if (!var8.hasNext()) {
                    if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
                        node.setFlowStyle(this.defaultFlowStyle);
                    } else {
                        node.setFlowStyle(bestStyle);
                    }

                    return node;
                }

                Property property = (Property)var8.next();
                Object memberValue = property.get(javaBean);
                Tag customPropertyTag = memberValue == null ? null : (Tag)this.classTags.get(memberValue.getClass());
                tuple = this.representJavaBeanProperty(javaBean, property, memberValue, customPropertyTag);
            } while(tuple == null);

            if (!((ScalarNode)tuple.getKeyNode()).isPlain()) {
                bestStyle = DumperOptions.FlowStyle.BLOCK;
            }

            Node nodeValue = tuple.getValueNode();
            if (!(nodeValue instanceof ScalarNode) || !((ScalarNode)nodeValue).isPlain()) {
                bestStyle = DumperOptions.FlowStyle.BLOCK;
            }

            value.add(tuple);
        }
    }

    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        ScalarNode nodeKey = (ScalarNode)this.representData(property.getName());
        boolean hasAlias = this.representedObjects.containsKey(propertyValue);
        Node nodeValue = this.representData(propertyValue);
        if (propertyValue != null && !hasAlias) {
            NodeId nodeId = nodeValue.getNodeId();
            if (customTag == null) {
                if (nodeId == NodeId.scalar) {
                    if (property.getType() != Enum.class && propertyValue instanceof Enum) {
                        nodeValue.setTag(Tag.STR);
                    }
                } else {
                    if (nodeId == NodeId.mapping && property.getType() == propertyValue.getClass() && !(propertyValue instanceof Map) && !nodeValue.getTag().equals(Tag.SET)) {
                        nodeValue.setTag(Tag.MAP);
                    }

                    this.checkGlobalTag(property, nodeValue, propertyValue);
                }
            }
        }

        return new NodeTuple(nodeKey, nodeValue);
    }

    protected void checkGlobalTag(Property property, Node node, Object object) {
        if (!object.getClass().isArray() || !object.getClass().getComponentType().isPrimitive()) {
            Class<?>[] arguments = property.getActualTypeArguments();
            if (arguments != null) {
                Class t;
                Iterator iter;
                Iterator var9;
                if (node.getNodeId() == NodeId.sequence) {
                    t = arguments[0];
                    SequenceNode snode = (SequenceNode)node;
                    Iterable<Object> memberList = Collections.EMPTY_LIST;
                    if (object.getClass().isArray()) {
                        memberList = Arrays.asList((Object[])object);
                    } else if (object instanceof Iterable) {
                        memberList = (Iterable)object;
                    }

                    iter = ((Iterable)memberList).iterator();
                    if (iter.hasNext()) {
                        var9 = snode.getValue().iterator();

                        while(var9.hasNext()) {
                            Node childNode = (Node)var9.next();
                            Object member = iter.next();
                            if (member != null && t.equals(member.getClass()) && childNode.getNodeId() == NodeId.mapping) {
                                childNode.setTag(Tag.MAP);
                            }
                        }
                    }
                } else if (object instanceof Set) {
                    t = arguments[0];
                    MappingNode mnode = (MappingNode)node;
                    iter = mnode.getValue().iterator();
                    Set<?> set = (Set)object;
                    var9 = set.iterator();

                    while(var9.hasNext()) {
                        Object member = var9.next();
                        NodeTuple tuple = (NodeTuple)iter.next();
                        Node keyNode = tuple.getKeyNode();
                        if (t.equals(member.getClass()) && keyNode.getNodeId() == NodeId.mapping) {
                            keyNode.setTag(Tag.MAP);
                        }
                    }
                } else if (object instanceof Map) {
                    t = arguments[0];
                    Class<?> valueType = arguments[1];
                    MappingNode mnode = (MappingNode)node;
                    iter = mnode.getValue().iterator();

                    while(iter.hasNext()) {
                        NodeTuple tuple = (NodeTuple)iter.next();
                        this.resetTag(t, tuple.getKeyNode());
                        this.resetTag(valueType, tuple.getValueNode());
                    }
                }
            }

        }
    }

    private void resetTag(Class<? extends Object> type, Node node) {
        Tag tag = node.getTag();
        if (tag.matches(type)) {
            if (Enum.class.isAssignableFrom(type)) {
                node.setTag(Tag.STR);
            } else {
                node.setTag(Tag.MAP);
            }
        }

    }

    protected Set<Property> getProperties(Class<? extends Object> type) {
        return this.typeDefinitions.containsKey(type) ? ((TypeDescription)this.typeDefinitions.get(type)).getProperties() : this.getPropertyUtils().getProperties(type);
    }

    protected class RepresentJavaBean implements Represent {
        protected RepresentJavaBean() {
        }

        public Node representData(Object data) {
            return org.yaml.snakeyaml.representer.Representer.this.representJavaBean(org.yaml.snakeyaml.representer.Representer.this.getProperties(data.getClass()), data);
        }
    }
}

