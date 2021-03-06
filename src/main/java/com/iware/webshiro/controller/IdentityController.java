/*
 * This file is licensed to you under the BSD License, (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.opensource.org/licenses/bsd-license.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.iware.webshiro.controller;

import java.util.Locale;

import javax.validation.Valid;
import javax.validation.Validator;

import com.iware.webshiro.domain.Identity;
import com.iware.webshiro.form.RegistrationForm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.iware.webshiro.form.LoginForm;
import com.iware.webshiro.service.IdentityService;

/**
 * This little bit of code handles login, registration, etc.
 * 
 * @author Bubba Hines <bubba@hines57.com>
 * @since 0.02
 */
@Controller
@RequestMapping(value = "/identity")
public class IdentityController {

  @Autowired(required = true)
  private IdentityService identityService;

  private Validator validator;

  public Validator getValidator() {
    return validator;
  }

  @Autowired
  public void setValidator(Validator validator) {
    this.validator = validator;
  }

  private static final Logger logger = LoggerFactory.getLogger(IdentityController.class);

  /**
   * Show the Login form
   * 
   * @param locale
   * @param model
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, value = { "/login" })
  public String login(Locale locale, Model model) {
    logger.debug("enter into login");

    model.addAttribute("loginForm", new LoginForm());

    logger.debug("exit login");

    return "identity/login";
  }

  /*
   * logout
   * @param locale
   * @param model
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, value = {"logout"})
  public String logout(Locale locale, Model model){
    logger.debug("enter into logout");

    // This gets the current subject from shiro
    Subject currentUser = SecurityUtils.getSubject();
    currentUser.logout(); //removes all identifying information and invalidates their session too.

    logger.debug("exit logout");

    return "home";
  }
  /**
   * Shows the registration form.
   * 
   * @param locale
   * @param model
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, value = { "/registration" })
  public String registration(Locale locale, Model model) {
    logger.info("Entering Registration");
    model.addAttribute("registration", new RegistrationForm());
    return "identity/registration";
  }

  /**
   * Handles the submission from the registration form.
   * 
   * @param registration
   * @param result
   * @param model
   * @return
   */
  @RequestMapping(method = RequestMethod.POST, value = { "/register" })
  public String register(@ModelAttribute(value = "registration") @Valid RegistrationForm registration, BindingResult result, Model model) {
    logger.info("Entering Register");

    if (result.hasErrors()) {

      return "identity/registration";
    }

    Identity identity = this.identityService.registerIdentity(registration);

    model.addAttribute("registration", registration);
    model.addAttribute("identity", identity);
    return "identity/register";
  }

  /**
   * Logs the user in, handles submission from the login form.
   * 
   * @param loginForm
   * @param result
   * @param model
   * @return
   */
  @RequestMapping(method = RequestMethod.POST, value = { "/authenticate" })
  public String register(@ModelAttribute(value = "loginForm") @Valid LoginForm loginForm, BindingResult result, Model model) {
    logger.info("Entering Authenticate");

    if (result.hasErrors()) {

      return "identity/login";
    }

    UsernamePasswordToken token = new UsernamePasswordToken(loginForm.getUsername(), loginForm.getPassphrase());

    // Remember Me built-in, just do this
    // TODO: Make this a user option instead of hard coded in.
    token.setRememberMe(true);

    Subject currentUser = SecurityUtils.getSubject();

    try {
      currentUser.login(token);
      logger.info("AUTH SUCCESS");
    } catch (AuthenticationException ae) {
      logger.info("AUTH MSSG: " + ae.getMessage());
    }

    if (currentUser.isAuthenticated()) {
      return "redirect:/index";
    }

    return "identity/login";
  }

  /**
   *
   *
   *
   */
  @RequestMapping(method = RequestMethod.GET, value = {"/unauthorized"})
  public String unauthorized(Locale locale, Model model){
    logger.debug("enter into unauthorized");

    // This gets the current subject from shiro
    Subject currentUser = SecurityUtils.getSubject();
    currentUser.logout(); //removes all identifying information and invalidates their session too.

    logger.debug("exit unauthorized");

    return "identity/unauthorized";
  }

}
