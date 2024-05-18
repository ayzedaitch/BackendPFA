package com.pfa.BackendPFA.model;

import com.pfa.BackendPFA.entity.Tourist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private String content;
    private String touristEmail;
    private int postId;
}
