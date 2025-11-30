package com.gamestore.gamecatalog.repository;

import com.gamestore.gamecatalog.entity.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    private Comment testComment;

    @BeforeEach
    void setUp() {
        testComment = new Comment();
        testComment.setJuegoId(1L);
        testComment.setUsuarioId(1L);
        testComment.setUsuarioNombre("Test User");
        testComment.setContenido("Test comment content");
        testComment.setIsHidden(false);
    }

    @Test
    void testSaveComment() {
        Comment saved = commentRepository.save(testComment);
        
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getJuegoId());
        assertEquals("Test comment content", saved.getContenido());
    }

    @Test
    void testFindByJuegoIdAndIsHiddenFalseOrderByCreatedAtDesc() {
        Comment visibleComment = new Comment();
        visibleComment.setJuegoId(1L);
        visibleComment.setUsuarioId(1L);
        visibleComment.setUsuarioNombre("User");
        visibleComment.setContenido("Visible");
        visibleComment.setIsHidden(false);
        
        Comment hiddenComment = new Comment();
        hiddenComment.setJuegoId(1L);
        hiddenComment.setUsuarioId(2L);
        hiddenComment.setUsuarioNombre("User");
        hiddenComment.setContenido("Hidden");
        hiddenComment.setIsHidden(true);
        
        commentRepository.save(visibleComment);
        commentRepository.save(hiddenComment);
        
        List<Comment> visibleComments = 
            commentRepository.findByJuegoIdAndIsHiddenFalseOrderByCreatedAtDesc(1L);
        
        assertFalse(visibleComments.isEmpty());
        assertTrue(visibleComments.stream().allMatch(c -> !c.getIsHidden()));
    }

    @Test
    void testFindByUsuarioIdOrderByCreatedAtDesc() {
        Comment comment1 = new Comment();
        comment1.setJuegoId(1L);
        comment1.setUsuarioId(1L);
        comment1.setUsuarioNombre("User");
        comment1.setContenido("Comment 1");
        comment1.setIsHidden(false);
        
        Comment comment2 = new Comment();
        comment2.setJuegoId(2L);
        comment2.setUsuarioId(1L);
        comment2.setUsuarioNombre("User");
        comment2.setContenido("Comment 2");
        comment2.setIsHidden(false);
        
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        
        List<Comment> comments = commentRepository.findByUsuarioIdOrderByCreatedAtDesc(1L);
        
        assertFalse(comments.isEmpty());
        assertTrue(comments.stream().allMatch(c -> c.getUsuarioId().equals(1L)));
    }

    @Test
    void testFindByIdAndJuegoId() {
        Comment saved = commentRepository.save(testComment);
        
        Optional<Comment> found = commentRepository.findByIdAndJuegoId(
            saved.getId(), saved.getJuegoId());
        
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void testCountByJuegoIdAndIsHiddenFalse() {
        Comment visible1 = new Comment();
        visible1.setJuegoId(1L);
        visible1.setUsuarioId(1L);
        visible1.setUsuarioNombre("User");
        visible1.setContenido("Visible 1");
        visible1.setIsHidden(false);
        
        Comment visible2 = new Comment();
        visible2.setJuegoId(1L);
        visible2.setUsuarioId(2L);
        visible2.setUsuarioNombre("User");
        visible2.setContenido("Visible 2");
        visible2.setIsHidden(false);
        
        commentRepository.save(visible1);
        commentRepository.save(visible2);
        
        long count = commentRepository.countByJuegoIdAndIsHiddenFalse(1L);
        
        assertTrue(count >= 2);
    }
}

