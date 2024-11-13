package com.example.springapi.shared.helpers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class Factory {
    private final ModelMapper _modelMapper;

    public Factory(ModelMapper modelMapper) {
        _modelMapper = modelMapper;
    }

    /**
     * mapper function
     */
    public <T, D> Object map(Object source, Class<T> targetClass) {
        if (source instanceof List) {
            var sourceList = (List<D>)source;
            if (!sourceList.isEmpty()) {
                List<T> listDesc = new ArrayList<>();
                sourceList.forEach(x -> {
                    listDesc.add(_modelMapper.map(x, targetClass));
                });
                return listDesc;
            } else {
                return new ArrayList<T>();
            }
        } else {
            return _modelMapper.map(source, targetClass);
        }
    }

    /**
     * Check uuid input is null or empty
     */
    public static boolean isNullOrEmptyId(UUID uuid) {
        // Kiểm tra UUID có phải là null hoặc UUID rỗng (00000000-0000-0000-0000-000000000000)
        return uuid == null || uuid.equals(new UUID(0L, 0L));
    }

}
