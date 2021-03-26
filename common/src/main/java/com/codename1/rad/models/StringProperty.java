/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import static com.codename1.rad.models.ContentType.Text;

/**
 * A {@link Property} containing a {@link String} value.
 * @author shannah
 * 
 * @see EntityType#string(com.codename1.rad.models.Attribute...) 
 */
public class StringProperty extends AbstractProperty<String> {
    public StringProperty() {
        super(Text);
    }
}
