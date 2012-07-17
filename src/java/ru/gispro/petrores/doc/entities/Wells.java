/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.entities;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author fkravchenko
 */
@XmlRootElement
public class Wells {
    private List<Well> _list;
    private Long _count;

    public Wells() {
    }

    public Wells(List<Well> list, Long count) {
        _list = list;
        _count = count;
    }

    @XmlElement
    public Long getTotal() {
        return _count;
    }

    @XmlElement
    public List<Well> getWells() {
        return _list;
    }
}
