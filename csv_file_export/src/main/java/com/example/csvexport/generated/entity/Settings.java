package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "settings")
public class Settings extends AbstractSettings {

    /** */
    @Column(name = "setting_name")
    public String settingName;

    /** */
    @Column(name = "setting_value")
    public String settingValue;
}
