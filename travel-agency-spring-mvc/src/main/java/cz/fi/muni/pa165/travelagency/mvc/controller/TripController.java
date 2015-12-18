package cz.fi.muni.pa165.travelagency.mvc.controller;

import cz.fi.muni.pa165.travelagency.dto.TripDTO;
import cz.fi.muni.pa165.travelagency.facade.TripFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author Michal Holic
 */
@Controller
@RequestMapping("/trip")
public class TripController {

	@Autowired
	private TripFacade tripFacade;

	final static Logger log = LoggerFactory.getLogger(TripController.class);

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String trip(@PathVariable long id, Model model) {
		model.addAttribute("trip", tripFacade.getById(id));
		return "trip/trip";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newTrip(Model model) {
		model.addAttribute("tripCreate", new TripDTO());
		return "trip/new";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String createTrip(@Valid @ModelAttribute("tripCreate") TripDTO formBean, BindingResult bindingResult,
						 Model model, RedirectAttributes redirectAttributes, UriComponentsBuilder uriBuilder) {
		if (bindingResult.hasErrors()) {
			for (ObjectError ge : bindingResult.getGlobalErrors()) {
				log.trace("ObjectError: {}", ge);
			}
			for (FieldError fe : bindingResult.getFieldErrors()) {
				model.addAttribute(fe.getField() + "_error", true);
				log.trace("FieldError: {}", fe);
			}
			return "trip/new";
		}
		try {
			tripFacade.create(formBean);
			redirectAttributes.addFlashAttribute("alert_success", "Trip " + formBean.getDestination() + " was created");
		} catch (JpaSystemException e) {
			redirectAttributes.addFlashAttribute("alert_warning", e.getMessage());
		}
		return "redirect:" + uriBuilder.path("/index").toUriString();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	public String deleteTrip(@PathVariable long id, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {
		TripDTO tripDTO = tripFacade.getById(id);
		if (tripDTO.getReservations().size() != 0) {
			redirectAttributes.addFlashAttribute("alert_warning", "Trip id " + id + " was not deleted because there is a reservation to it.");
			return "redirect:" + uriBuilder.path("/index").toUriString();
		}
		if (tripDTO.getExcursions().size() != 0) {
			redirectAttributes.addFlashAttribute("alert_warning", "Trip id " + id + " was not deleted because it still contains excursions.");
			return "redirect:" + uriBuilder.path("/index").toUriString();
		}
		try {
			tripFacade.delete(id);
			redirectAttributes.addFlashAttribute("alert_success", "Trip id " + id + " was deleted.");
		} catch (JpaSystemException e) {
			redirectAttributes.addFlashAttribute("alert_warning", e.getMessage());
		}
		return "redirect:" + uriBuilder.path("/index").toUriString();
	}
}