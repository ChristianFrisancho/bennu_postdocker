package cl.bennu.tablasbennu.enums.handler;

public class EnumTypeHandler<E extends Enum<E>> extends org.apache.ibatis.type.EnumTypeHandler<E> {

    public EnumTypeHandler(Class<E> tipo) {
        super(tipo);
    }

}
