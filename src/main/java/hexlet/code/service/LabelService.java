package hexlet.code.service;

import hexlet.code.dto.LabelCreateDto;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.LabelUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper mapper;

    public List<LabelDto> getAll() {
        return labelRepository.findAll().stream().map(mapper::map).toList();
    }

    public LabelDto show(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        return mapper.map(label);
    }

    public LabelDto create(LabelCreateDto dto){
        Label label = mapper.map(dto);
        labelRepository.save(label);
        return mapper.map(label);
    }

    public LabelDto update(LabelUpdateDto dto, Long id){
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        mapper.update(dto, label);
        labelRepository.save(label);
        return mapper.map(label);
    }

    public void delete(Long id){
        labelRepository.deleteById(id);
    }

}
