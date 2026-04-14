package com.example.flashcard.controller;

import com.example.flashcard.domain.Flashcard;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class FlashcardController {

    private static final String FLASHCARDS_SESSION_KEY = "flashcards";

    @GetMapping("/")
    public String index() {
        return "redirect:/flashcards";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/flashcards/new")
    public String showForm(Model model, HttpSession session) {
        if (session.getAttribute("loginUser") == null) {
            session.setAttribute("loginUser", "방문자_" + System.currentTimeMillis() % 1000);
        }
        
        model.addAttribute("flashcard", new Flashcard());
        return "flashcard-form";
    }

    @PostMapping("/flashcards/new")
    public String saveFlashcard(@ModelAttribute Flashcard flashcard, HttpSession session) {
        List<Flashcard> flashcards = getFlashcardsFromSession(session);
        flashcard.setId(System.currentTimeMillis());
        flashcard.setCreatedTime(LocalDateTime.now());
        flashcard.setModifiedTime(LocalDateTime.now());
        flashcards.add(flashcard);
        session.setAttribute(FLASHCARDS_SESSION_KEY, flashcards); 
        return "redirect:/flashcards";
    }

    @GetMapping("/flashcards")
    public String listFlashcards(Model model, HttpSession session) {
        model.addAttribute("flashcards", getFlashcardsFromSession(session));
        return "flashcard-list";
    }

    @GetMapping("/flashcards/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        List<Flashcard> flashcards = getFlashcardsFromSession(session);
        Optional<Flashcard> flashcard = flashcards.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst();
        
        if (flashcard.isPresent()) {
            model.addAttribute("flashcard", flashcard.get());
            return "flashcard-form";
        }
        return "redirect:/flashcards";
    }

    @PostMapping("/flashcards/edit/{id}")
    public String updateFlashcard(@PathVariable Long id, @ModelAttribute Flashcard updatedCard, HttpSession session) {
        List<Flashcard> flashcards = getFlashcardsFromSession(session);
        for (int i = 0; i < flashcards.size(); i++) {
            Flashcard existingCard = flashcards.get(i);
            if (existingCard.getId().equals(id)) {
                updatedCard.setId(id);
                updatedCard.setCreatedTime(existingCard.getCreatedTime()); // 기존 생성시간 유지
                updatedCard.setModifiedTime(LocalDateTime.now()); // 수정시간 갱신
                flashcards.set(i, updatedCard);
                break;
            }
        }
        session.setAttribute(FLASHCARDS_SESSION_KEY, flashcards);
        return "redirect:/flashcards";
    }

    @PostMapping("/flashcards/delete/{id}")
    public String deleteFlashcard(@PathVariable Long id, HttpSession session) {
        List<Flashcard> flashcards = getFlashcardsFromSession(session);
        flashcards.removeIf(f -> f.getId().equals(id));
        session.setAttribute(FLASHCARDS_SESSION_KEY, flashcards);
        return "redirect:/flashcards";
    }

    @GetMapping("/flashcards/practice")
    public String practice(Model model, HttpSession session) {
        List<Flashcard> flashcards = getFlashcardsFromSession(session);
        if (flashcards.isEmpty()) {
            return "redirect:/flashcards/new";
        }
        model.addAttribute("flashcards", flashcards);
        return "flashcard-practice";
    }

    @SuppressWarnings("unchecked")
    private List<Flashcard> getFlashcardsFromSession(HttpSession session) {
        List<Flashcard> flashcards = (List<Flashcard>) session.getAttribute(FLASHCARDS_SESSION_KEY);
        if (flashcards == null) {
            flashcards = new ArrayList<>();
        }
        return flashcards;
    }
}
