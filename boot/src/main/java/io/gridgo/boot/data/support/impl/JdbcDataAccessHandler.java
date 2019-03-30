package io.gridgo.boot.data.support.impl;

import io.gridgo.bean.BObject;
import io.gridgo.boot.data.support.annotations.JdbcProduce;
import io.gridgo.framework.support.Message;
import lombok.Getter;

@Getter
public class JdbcDataAccessHandler extends AbstractDataAccessHandler<JdbcProduce> {

    public JdbcDataAccessHandler() {
        super(JdbcProduce.class);
    }

    @Override
    protected Message buildMessage(JdbcProduce annotation, Object[] args) {
        var headers = BObject.ofEmpty();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                headers.setAny((i + 1) + "", args[i]);
            }
        }
        var query = context.getRegistry().substituteRegistriesRecursive(annotation.value());
        return Message.ofAny(headers, query);
    }
}
