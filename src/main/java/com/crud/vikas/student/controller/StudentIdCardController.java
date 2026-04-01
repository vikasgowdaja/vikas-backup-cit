package com.crud.vikas.student.controller;

import com.crud.vikas.student.model.StudentIdCard;
import com.crud.vikas.student.service.StudentPhotoStorageService;
import com.crud.vikas.student.service.StudentIdCardService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/students")
public class StudentIdCardController {

    private final StudentIdCardService service;
    private final StudentPhotoStorageService photoStorageService;

    public StudentIdCardController(StudentIdCardService service, StudentPhotoStorageService photoStorageService) {
        this.service = service;
        this.photoStorageService = photoStorageService;
    }

    @GetMapping
    public String listCards(Model model) {
        model.addAttribute("cards", service.findAll());
        return "students/list";
    }

    @GetMapping("/list")
    public String listCardsAlias(Model model) {
        return listCards(model);
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        StudentIdCard card = new StudentIdCard();
        card.setValidTill(LocalDate.now().plusYears(1));
        model.addAttribute("studentCard", card);
        model.addAttribute("editMode", false);
        model.addAttribute("photoPreviewPath", "/assets/student-placeholder.svg");
        return "students/form";
    }

    @PostMapping
    public String createCard(@Valid @ModelAttribute("studentCard") StudentIdCard studentCard,
            @RequestParam("photoFile") MultipartFile photoFile,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (service.existsByStudentCode(studentCard.getStudentCode())) {
            bindingResult.rejectValue("studentCode", "duplicate", "Student code already exists");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", false);
            model.addAttribute("photoPreviewPath", "/assets/student-placeholder.svg");
            return "students/form";
        }

        if (photoFile != null && !photoFile.isEmpty()) {
            studentCard.setPhotoFileName(photoStorageService.storePhoto(photoFile));
        }
        service.create(studentCard);
        redirectAttributes.addFlashAttribute("successMessage", "Student ID card created successfully.");
        return "redirect:/students";
    }

    @GetMapping("/{id:\\d+}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            StudentIdCard card = service.findById(id);
            model.addAttribute("studentCard", card);
            model.addAttribute("editMode", true);
            model.addAttribute("photoPreviewPath", photoStorageService.publicPath(card.getPhotoFileName()));
            return "students/form";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/students";
        }
    }

    @PostMapping("/{id:\\d+}")
    public String updateCard(@PathVariable Long id,
            @Valid @ModelAttribute("studentCard") StudentIdCard studentCard,
            @RequestParam("photoFile") MultipartFile photoFile,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            StudentIdCard existing = service.findById(id);
            if (!existing.getStudentCode().equals(studentCard.getStudentCode())
                    && service.existsByStudentCode(studentCard.getStudentCode())) {
                bindingResult.rejectValue("studentCode", "duplicate", "Student code already exists");
            }

            if (bindingResult.hasErrors()) {
                model.addAttribute("editMode", true);
                String preview = photoStorageService.publicPath(existing.getPhotoFileName());
                model.addAttribute("photoPreviewPath", preview);
                return "students/form";
            }

            if (studentCard.getValidTill() == null) {
                studentCard.setValidTill(existing.getValidTill());
            }

            if (photoFile != null && !photoFile.isEmpty()) {
                studentCard.setPhotoFileName(photoStorageService.storePhoto(photoFile));
            } else {
                studentCard.setPhotoFileName(existing.getPhotoFileName());
            }

            service.update(id, studentCard);
            redirectAttributes.addFlashAttribute("successMessage", "Student ID card updated successfully.");
            return "redirect:/students";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/students";
        }
    }

    @PostMapping("/{id:\\d+}/delete")
    public String deleteCard(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student ID card deleted successfully.");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/students";
    }

    @GetMapping("/{id:\\d+}")
    public String viewCard(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            StudentIdCard card = service.findById(id);
            model.addAttribute("card", card);
            model.addAttribute("studentPhotoPath", photoStorageService.publicPath(card.getPhotoFileName()));
            model.addAttribute("collegeLogoPath", "/assets/CIT.svg");
            return "students/view";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/students";
        }
    }
}
