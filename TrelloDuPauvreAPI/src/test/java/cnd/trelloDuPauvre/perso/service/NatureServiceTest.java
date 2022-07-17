package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Nature;
import cnd.trelloDuPauvre.perso.repository.NatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NatureServiceTest {

    @Mock
    NatureRepository natureRepository;

    NatureService underTest;

    @BeforeEach
    void setUp() {
        underTest = new NatureService(natureRepository);
    }

    @Test
    void can_call_getAllNature() {
        //given
        Nature nature1 = new Nature("name", "label");
        Nature nature2 = new Nature("name", "label");
        ArrayList<Nature> natures = new ArrayList<>();
        natures.add(nature1);
        natures.add(nature2);
        // When
        when(natureRepository.findAll()).thenReturn(natures);
        List<Nature> result = underTest.getAllNatures();
        //Then
        verify(natureRepository).findAll();
        assertThat(result).isEqualTo(natures);
    }

    @Test
    void can_call_getNatureById_with_validId() {
        //given
        int id = new Random().nextInt(100);
        Nature nature = new Nature("name", "label");
        //when
        when(natureRepository.findById(any(int.class))).thenReturn(Optional.of(nature));
        Nature result = underTest.getNatureById(id);
        //then
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(natureRepository).findById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(result).isEqualTo(nature);
    }

    @Test
    void getNatureById_with_notExistingId_shouldThrow_NotFoundException() {
        //given
        int id = new Random().nextInt(100);
        //when
        //then
        assertThatThrownBy(() -> underTest.getNatureById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Nature with id " + id + " not found");
    }

    @Test
    void can_call_createNature() {
        //given
        Nature nature = new Nature("name", "label");
        //when
        when(natureRepository.save(any(Nature.class))).thenReturn(nature);
        Nature result = underTest.createNature(nature);
        //then
        ArgumentCaptor<Nature> natureCaptor = ArgumentCaptor.forClass(Nature.class);
        verify(natureRepository).save(natureCaptor.capture());

        Nature capturedNature = natureCaptor.getValue();
        assertThat(capturedNature).isEqualTo(nature);
        assertThat(result).isEqualTo(nature);
    }

    @Test
    void can_call_updateNature_with_validId() {
        //given
        Nature nature = new Nature("name", "label");
        int id = new Random().nextInt(100);
        //when
        when(natureRepository.findById(any(int.class))).thenReturn(Optional.of(nature));
        when(natureRepository.save(any(Nature.class))).thenReturn(nature);
        Nature result = underTest.updateNature(id, nature);
        //then
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Nature> natureCaptor = ArgumentCaptor.forClass(Nature.class);
        verify(natureRepository).findById(idCaptor.capture());
        verify(natureRepository).save(natureCaptor.capture());

        int capturedId = idCaptor.getValue();
        Nature capturedNature = natureCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(capturedNature).isEqualTo(nature);
        assertThat(result).isEqualTo(nature);
    }

    @Test
    void updateNature_with_notExistingId_shouldThrow_NotFoundException(){
        //given
        int id = new Random().nextInt(100);
        Nature nature = new Nature("name", "label");
        //when
        //then
        assertThatThrownBy(() -> underTest.updateNature(id, nature))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Nature with id " + id + " not found");
    }

    @Test
    void can_call_deleteNature_with_validId_should_return_true() {
        //given
        int id = new Random().nextInt(100);
        Nature nature = new Nature("name", "label");
        //when
        when(natureRepository.findById(any(int.class))).thenReturn(Optional.of(nature));
        Boolean result = underTest.deleteNature(id);
        //then
        ArgumentCaptor<Integer> idCaptorFindBy = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(natureRepository).findById(idCaptorFindBy.capture());
        verify(natureRepository).deleteById(idCaptorDeleteBy.capture());

        int capturedFindById = idCaptorFindBy.getValue();
        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedFindById).isEqualTo(id);
        assertThat(capturedDeleteById).isEqualTo(id);
        assertThat(result).isEqualTo(true);
    }

    @Test
    void deleteNature_with_notExistingId_should_return_false() {
        //given
        int id = new Random().nextInt(100);
        //when
        Boolean result = underTest.deleteNature(id);
        //then
        assertThat(result).isEqualTo(false);
    }
}
