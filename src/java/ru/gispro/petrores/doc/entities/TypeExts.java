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
public class TypeExts {
    private List<TypeExt> _list;
    private long _count;

    public TypeExts() {
    }

    public TypeExts(List<TypeExt> list, long count) {
        _list = list;
        _count = count;
    }

    @XmlAttribute(name="totalCount")
    public long getTotalCount() {
        return _count;
    }

    @XmlElement
    public List<TypeExt> getTypeExts() {
        return _list;
    }
}
