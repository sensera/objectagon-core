package org.objectagon.core.storage.entity;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Message;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by christian on 2016-02-24.
 */
public class DataVersions<I extends Identity, V extends Version> extends AbstractData<I,V> {

    private List<TreeNode> tree = new LinkedList<>();
    private Version.Create<V> create;

    public DataVersions(I identity, V version, Message.Values values, Version.Create<V> create) {
        super(identity, version);
        this.create = create;
        TreeNode treeNode = new TreeNode(values);
    }

    @Override
    public Iterable<Message.Value> values() {
        return null;
    }

    private class TreeNode {
        V version;
        List<TreeNode> children = new LinkedList<>();

        public TreeNode(Message.Values values) {
            values.values().forEach(this::add);
        }

        public void add(Message.Value value) {
            if (version==null) {
                if (!value.getField().equals(Version.VERSION))
                    throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.INCONSISTENCY);
                version = create.create(value);
            } else {
                children.add(new TreeNode(value.asValues()));
            }
        }

    }


}
