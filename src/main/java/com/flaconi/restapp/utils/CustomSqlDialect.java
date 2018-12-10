package com.flaconi.restapp.utils;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.BinaryType;
import org.hibernate.type.StringType;

/**
 * Custom SQL dialect to register some specific MySQL functions
 */
public class CustomSqlDialect extends MySQL5InnoDBDialect {

    public CustomSqlDialect() {
        registerFunction("uuid_to_bin", new SQLFunctionTemplate(BinaryType.INSTANCE, "uuid_to_bin(?1)"));
        registerFunction("BIN_TO_UUID", new SQLFunctionTemplate(StringType.INSTANCE, "BIN_TO_UUID(?1)"));
    }
}