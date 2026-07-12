package ru.zipprey.eventify.booking.service.booking.cancellation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.zipprey.eventify.booking.model.BookingsBatch;
import ru.zipprey.eventify.booking.model.entity.Booking;
import ru.zipprey.eventify.booking.repository.BookingRepository;

import java.util.LinkedList;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingCancellationServiceImpl implements BookingCancellationService {

    public static final String FREE_ERROR_MESSAGE = "Не удалось освободить нужное количество мест: " +
            "нужно={}, освобождено={}, eventId={}";
    private final BookingRepository repository;

    @Override
    public Optional<BookingsBatch> freeUp(Long eventId, int deficit) {
        var canceled = new LinkedList<Booking>();

        int freed = 0;
        if (freed < deficit) {
            var confirmed = repository
                    .findAllByEventIdAndConfirmedTrueOrderByCreatedAtDesc(eventId);

            for (var booking : confirmed) {
                if (freed >= deficit) break;
                canceled.add(booking);
                freed += booking.getTicketsCount();
            }
        }

        if (freed < deficit) {
            log.error(FREE_ERROR_MESSAGE, deficit, freed, eventId);
            return Optional.empty();
        }

        repository.deleteAll(canceled);
        return Optional.of(new BookingsBatch(canceled, freed));
    }
}
