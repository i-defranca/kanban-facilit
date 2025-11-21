package dev.challenge.unit;

import dev.challenge.entity.Base;
import dev.challenge.sluggable.SlugService;
import dev.challenge.sluggable.Sluggable;
import dev.challenge.sluggable.SluggableRepository;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

class UnitTestSetup {
    static void beforeEach(SluggableRepository<? extends Base> repository, SlugService<? extends Base> slugger) {
        SlugService<?> service = new SlugService<>(List.of(repository));
        Map<String, Base> store = new HashMap<>();

        lenient().when(slugger.generate(any())).thenAnswer(i -> service.generate((Sluggable) i.getArguments()[0]));

        lenient().when(repository.save(any())).thenAnswer(inv -> {
            Base a = inv.getArgument(0);
            if (a.getId() == null) {
                a.setId(UUID.randomUUID());
                a.setSlug(service.generate(a));
            }
            store.putIfAbsent(a.getSlug(), a);
            return a;
        });

        lenient().when(repository.existsBySlug(any(String.class)))
                 .thenAnswer(i -> store.get((String) i.getArguments()[0]) != null);

        lenient().when(repository.findBySlug(any(String.class)))
                 .thenAnswer(i -> Optional.ofNullable(store.get((String) i.getArguments()[0])));
        lenient().when(repository.findAll()).thenAnswer(inv -> new ArrayList<>(store.values()));
        lenient().doAnswer(i -> store.remove((String) i.getArgument(0)))
                 .when(repository)
                 .deleteBySlug(any(String.class));
    }
}
