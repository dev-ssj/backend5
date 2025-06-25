package com.example.backendproject.comment.controller;

import com.example.backendproject.comment.dto.CommentDTO;
import com.example.backendproject.comment.service.CommentService;
import com.example.backendproject.security.core.CustomUserDetails;
import com.example.backendproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;


    @PostMapping
    public ResponseEntity<CommentDTO> save(@RequestBody CommentDTO commentDTO) {

        CommentDTO response = commentService.saveComment(commentDTO);
        return ResponseEntity.ok(response);
    }

    // 게시글의 전체 댓글+대댓글 계층 조회
    @GetMapping
    public List<CommentDTO> getAllComments(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam Long boardId) throws UserPrincipalNotFoundException {
        Long userid = customUserDetails.getId();
        if(userRepository.findById(userid).isEmpty()){
            throw new UserPrincipalNotFoundException("해당 유저가 존재하지 않습니다.");
        }
        return commentService.findCommentsByBoardId(boardId); // 반드시 계층구조 반환!
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id); // id로 댓글 삭제
        return ResponseEntity.ok().build();
    }

}
