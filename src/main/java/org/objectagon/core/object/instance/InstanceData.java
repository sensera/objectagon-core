package org.objectagon.core.object.instance;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Converter;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.address.AddressList;
import org.objectagon.core.object.*;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.data.AbstractData;

import static org.objectagon.core.msg.message.VolatileAddressValue.address;
import static org.objectagon.core.utils.Util.concat;

/**
 * Created by christian on 2015-10-20.
 */
public class InstanceData extends AbstractData<InstanceIdentity, ObjectVersion> {

    private InstanceClassAddress instanceClassAddress;
    private AddressList<FieldValueAddress> values = new AddressList<>();
    private AddressList<RelationAddress> relations = new AddressList<>();

    public InstanceData(InstanceIdentity identity, ObjectVersion version, InstanceClassAddress instanceClassAddress) {
        super(identity, version);
        this.instanceClassAddress = instanceClassAddress;
    }

    //@Override
    public Iterable<Message.Value> values() {
        return concat(
                address(InstanceProtocol.FieldName.INSTANCE_CLASS, instanceClassAddress),
                address(InstanceProtocol.FieldName.VALUES, values),
                address(InstanceProtocol.FieldName.RELATIONS, relations)
        );
    }

    @Override
    public void convert(Converter.FromData<Data<InstanceIdentity, ObjectVersion>> fromData) {

    }

    public FieldValueAddress getValueByField(FieldAddress fieldAddress) throws UserException {
        return values.select(fieldAddress::sameField)
                .orElseThrow(() -> new UserException(ErrorClass.INSTANCE, ErrorKind.FIELD_NOT_FOUND, address(fieldAddress)));
    }
}
