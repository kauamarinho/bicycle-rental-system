package org.example.repository.inmemory;

import org.springframework.stereotype.Repository;
import org.example.domain.model.Bicycle;
import org.example.repository.BicycleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryBicycleRepository implements BicycleRepository {

    private List<Bicycle> bicycles = new ArrayList<>();

    @Override
    public void save(Bicycle bicycle) {
        bicycles.add(bicycle);
    }

    @Override
    public List<Bicycle> findAll() {
        return List.copyOf(bicycles);
    }

    @Override
    public Optional<Bicycle> findById(int id) {
        for (Bicycle b : bicycles) {
            if (b.getId() == id) return Optional.of(b);
        }
        return Optional.empty();
    }
}
