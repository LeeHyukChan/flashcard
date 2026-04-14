package com.example.flashcard.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flashcard {

    private Long id;

    private Long memberId;

    private String front;

    private String back;

    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;

    private Long modifiedId;

}
