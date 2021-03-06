/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.vavmar.reporteditor.actions;

import cz.muni.fi.vavmar.reporteditor.MainScene;
import cz.muni.fi.vavmar.reporteditor.widgets.ImageWidgetWraper;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Martin
 */
public class WidgetSelectionAction extends WidgetAction.Adapter {
    private static final Logger logger = LogManager.getLogger(WidgetSelectionAction.class);
    
    private MainScene scene;

    public WidgetSelectionAction(MainScene scene) {
        this.scene = scene;
    }
    
    
    @Override
    public State mouseClicked(Widget widget, WidgetMouseEvent event) {
        Border selectedBorder = BorderFactory.createDashedBorder(Color.ORANGE, 6, 2, 1, false);
        BorderFactory.createDashedBorder(Color.black, 1, 1, 1, false);
        logger.trace("Selected widget: " + widget);
        logger.trace("At: " + event.getPoint() + "CTRL pressed selection: " + event.isControlDown());

        
        Set<Widget> selectedWidgets = scene.getSelectedWidgets();
        logger.trace("Selected widgets: " + selectedWidgets);
        
        if( selectedWidgets == null ) selectedWidgets = new HashSet<Widget>();
        
        if(!event.isControlDown()) {  //without CRTL we want only select this widget (single selection)
            logger.trace("Invertion is not set performing single selection.");
            logger.trace("Widget bounds: " + widget.getBounds());
            selectedWidgets = scene.clearSelection();
            widget.setBorder( selectedBorder );
            //set Resize Action pro obrazek!!!
            selectedWidgets.add(widget);
            
            scene.setSelectedWidgets(selectedWidgets);
            
            if(widget instanceof ImageWidget){
//                widget.getActions().getActions().clear();
                widget.setBorder(org.netbeans.api.visual.border.BorderFactory.createResizeBorder(7));
//                widget.getActions().addAction(0, scene.getImageResizeAction());
                ( (ImageWidgetWraper) widget).activateResizeAction(true);
            }
        } else {
            if ( selectedWidgets.size() == 1 ) {    //There could be widget without multimovement set
                                                    //or just one widget with multimovement action set
                                                    //so simply clear selection and make new selection
                logger.trace("One widget is selected and invertion is set. Adding second widget.");
                //Save previously selected widget 
                Iterator<Widget> iterator = selectedWidgets.iterator();	//workaround to get widget from a set
                Widget previouslySelected = iterator.next();
                
                //clear the selection (remove border and action of selected widget)
                selectedWidgets = scene.clearSelection();
                
                //set multimovement to the previously selected widget
                previouslySelected.setBorder( selectedBorder );
                scene.setMultiMoveAction(previouslySelected, scene.getMultipleMovementAction());
                
                //set multimovement to the newly selected widget
                widget.setBorder( selectedBorder );
                scene.setMultiMoveAction(widget, scene.getMultipleMovementAction());
                
                //add both widgets to the new selection
                selectedWidgets.add(previouslySelected);
                selectedWidgets.add(widget);
                
            } else if ( selectedWidgets.size() >= 2 ){
                logger.trace("Two widgets are selected and invertion is set. Resolving adding/removing.");
                
                if( selectedWidgets.contains(widget) ){     //if widget is allready selected unselect it
                    selectedWidgets.remove(widget);
                    widget.setBorder(BorderFactory.createEmptyBorder());
                    widget.getActions().removeAction( scene.getMultipleMovementAction() );
                    
                } else {        //Otherwise select it
                    widget.setBorder(selectedBorder);
                    scene.setMultiMoveAction(widget, scene.getMultipleMovementAction() );
                    scene.getSelectedWidgets().add(widget);
                }
                
            } else {
                logger.warn("Something weird happend during selection of widgets!");
            }
            
        }
        
//        if( selectedWidgets.size() > 1 ){
//            if( selectedWidgets.contains(widget) && invertSelection ) {     //if widget is selected, deselec it
//                selectedWidgets.remove(widget);
//                widget.setBorder(BorderFactory.createEmptyBorder());
//                widget.getActions().removeAction( scene.getMultipleMovementAction() );
//                
//            } else {    //otherwise add it to the selection
//                widget.setBorder(BorderFactory.createDashedBorder(Color.BLUE, 1, 1));
//                Utils.setMultiMoveAction(widget, scene.getMultipleMovementAction());
//                
//                selectedWidgets.add(widget);
//            }
//        }
        
        logger.trace("Selected widgets: " + selectedWidgets.size() + " " + selectedWidgets);

        return State.REJECTED;  //we still need to allow moving of widget
    }

}
