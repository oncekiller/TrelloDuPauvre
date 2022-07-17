package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Nature;
import cnd.trelloDuPauvre.perso.repository.NatureRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class NatureService {
    @Autowired
    private NatureRepository natureRepository;

    public List<Nature> getAllNatures() {
        List<Nature> natures = natureRepository.findAll();
        return natures;
    }

    public Nature getNatureById(int natureId){
        Optional<Nature> nature = natureRepository.findById(natureId);

        if (nature.isPresent()){
            return nature.get();
        }else {
            throw new EntityNotFoundException("Nature with id " + natureId + " not found");
        }
    }

    public Nature createNature(Nature nature) {
        return  natureRepository.save(nature);
    }

    public Nature updateNature(int natureId, Nature nature){
        Optional<Nature> updatedNature = natureRepository.findById(natureId);

        if (updatedNature.isPresent()) {
            Nature newNature = updatedNature.get();
            newNature.setName(nature.getName());
            newNature.setLabel(nature.getLabel());

            return natureRepository.save(newNature);
        }else {
            throw new EntityNotFoundException("Nature with id " + natureId + " not found");
        }
    }

    public Boolean deleteNature(int natureId) {
        Optional<Nature> deletedNature = natureRepository.findById(natureId);

        if(deletedNature.isPresent()) {
            natureRepository.deleteById(natureId);
            return true;
        }else{
            return false;
        }
    }
}
