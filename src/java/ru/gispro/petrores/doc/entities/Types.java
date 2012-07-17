/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.entities;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author fkravchenko
 */
@XmlRootElement
public class Types {
    private List<Type> _list;
    private long _count;

    public Types() {
    }

    public Types(List<Type> list, long count) {
        _list = list;
        _count = count;
    }

    @XmlElement
    public long getTotal() {
        return _count;
    }

    @XmlElement
    public List<Type> getTypes() {
        return _list;
    }
}
