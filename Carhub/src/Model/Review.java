package Model;

import contract.Formatter;
import contract.Identifiable;
import java.util.UUID;
public final class Review implements Identifiable, Formatter {

    private final UUID id;
    private final UUID carId;
    private final User user; // 1. Artık sadece bir isim değil, bir KİŞİ var.
    private final int rating;
    private final String comment;
    private final String date;

    public Review(UUID id, UUID carId, User user, int rating, String comment, String date) {
        this.id = id;
        this.carId = carId;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    public UUID getCarId() { return carId; }

    public String getReviewerName() {
        return user.getUsername();
    }

    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getDate() { return date; }

    @Override
    public UUID getId() { return id; }

    @Override
    public String format() {
        return String.format(
                "YORUM -> ID: %s | Araba ID: %s | Yazan: %s (%s) | Puan: %d | Yorum: %s | Tarih: %s",
                id.toString().substring(0, 8),
                carId.toString().substring(0, 8),
                user.getUsername(),
                user.getRole(),
                rating,
                comment,
                date
        );
    }
}