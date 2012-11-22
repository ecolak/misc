/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import org.displaytag.decorator.TableDecorator;
import dao.CLCar;

/**
 *
 * @author ecolak
 */
public class CLCarDecorator extends TableDecorator {

    public CLCar getData() {
        return (CLCar)this.getCurrentRowObject();
    }

    public String getActions() {
        /*String url = "#";
        if(getData() != null) {
            url = getData().getListingUrl();
        }*/
        return "<a href=\"CarDetails.jsp?car_id=" + getData().getCarId().toString()
                + "\">Details</a>";
    }

}
