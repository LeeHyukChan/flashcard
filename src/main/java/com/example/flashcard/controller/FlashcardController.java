package com.example.flashcard.controller;

import com.example.flashcard.domain.Flashcard;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FlashcardController {

    private static final String FLASHCARDS_SESSION_KEY = "flashcards";

    @GetMapping("/")
    public String index() {
        return "redirect:/flashcards/new";
    }

    @GetMapping("/flashcards/new")
    public String showForm(Model model, HttpSession session) {
        // 테스트를 위해 세션에 가짜 유저 정보를 넣어봅니다.
        if (session.getAttribute("loginUser") == null) {
            session.setAttribute("loginUser", "방문자_" + System.currentTimeMillis() % 1000);
        }
        
        model.addAttribute("flashcard", new Flashcard());
        return "flashcard-form";
    }

    @PostMapping("/flashcards/new")
    public String saveFlashcard(@ModelAttribute Flashcard flashcard, HttpSession session) {
        List<Flashcard> flashcards = getFlashcardsFromSession(session);
        flashcards.add(flashcard);
        session.setAttribute(FLASHCARDS_SESSION_KEY, flashcards);
        return "redirect:/flashcards";
    }

    @GetMapping("/flashcards")
    public String listFlashcards(Model model, HttpSession session) {
        model.addAttribute("flashcards", getFlashcardsFromSession(session));
        return "flashcard-list";
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
