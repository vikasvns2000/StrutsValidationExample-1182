/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jwt.struts.form;


public class UserDispactForm extends org.apache.struts.action.ActionForm {

    private String message;
    /**
     *
     */
    public UserDispactForm() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

   
}
