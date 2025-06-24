package com.example.backendproject.comment.service;

import com.example.backendproject.board.entity.Board;
import com.example.backendproject.board.repository.BoardRepository;
import com.example.backendproject.comment.dto.CommentDTO;
import com.example.backendproject.comment.entity.Comment;
import com.example.backendproject.comment.repository.CommentRepository;
import com.example.backendproject.user.entity.User;
import com.example.backendproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;




    @Transactional
    public CommentDTO saveComment(CommentDTO commentDTO) {

        Board board = boardRepository.findById(commentDTO.getBoardId())
                .orElseThrow(()->new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Comment parent  = null;
        if (commentDTO.getParentId()!=null){
            parent = commentRepository.findById(commentDTO.getParentId())
                    .orElseThrow(()-> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        }

        // 🔥 유저 조회 후 프로필에서 username 가져오기
        User user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        String username = (user.getUserProfile() != null)
                ? user.getUserProfile().getUsername()
                : "익명";

        Comment comment = new Comment();
        comment.setUsername(username);
        comment.setContent(commentDTO.getContent());
        comment.setUserId(commentDTO.getUserId());
        comment.setBoard(board);
        comment.setParent(parent); //첫댓글이면 null 댓글에 댓글이면 id가 들어감

        Comment saveComment = commentRepository.save(comment);

        return toDTO(saveComment);
    }

    /** 댓글 전체 반환 **/
    @Transactional(readOnly = true)
    public List<CommentDTO> findCommentsByBoardId(Long boardId) {
        List<Comment> comments = commentRepository.findByBoardId(boardId);

        // Entity → DTO 변환
        List<CommentDTO> dtos = comments.stream() //리스트 안의 요소들을 하나씩 치러할수있는 파이프라인을 만듬
                .map(CommentDTO::fromEntity) //스트림의 각 요소를 함수로 변환  (Comment 객체를 CommentDTO로 변환)
                .collect(Collectors.toList()); //리스트로 수집함  (최종적으로 List<CommentDTO>를 얻음)
        return dtos;
    }

    @Transactional
    public void deleteComment(Long id){
        commentRepository.deleteById(id);
    }



    // 엔티티 → DTO 변환
    public CommentDTO toDTO(Comment entity) {
        CommentDTO dto = new CommentDTO();
        dto.setId(entity.getId());
        dto.setBoardId(entity.getBoard().getId());
        dto.setUserId(entity.getUserId());
        dto.setUsername(entity.getUsername());
        dto.setContent(entity.getContent());
        dto.setParentId(entity.getParent() != null ? entity.getParent().getId() : null);
        dto.setCreatedDate(entity.getCreated_date());
        dto.setUpdatedDate(entity.getUpdated_date());
        // 등록 API에선 children은 null로 반환 (조회 API에서만 재귀 변환)
        return dto;
    }
}
