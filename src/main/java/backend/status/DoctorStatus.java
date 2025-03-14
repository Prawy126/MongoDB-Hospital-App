package backend.status;

/**
 * Enum DoctorStatus reprezentuje status lekarza.
 * Lekarz może być dostępny lub niedostępny.
 * Dostępny oznacza, że lekarz jest w stanie przyjąć pacjenta.
 * Niedostępny oznacza, że lekarz nie jest w stanie przyjąć pacjenta.
 * Lekarz może być niedostępny z powodu urlopu, choroby lub innych przyczyn.
 * Lekarz może być dostępny, jeśli nie ma żadnych przeszkód w jego pracy.
 * Lekarz może zmienić swój status z dostępnego na niedostępny i odwrotnie.
 * Lekarz może zmienić swój status w dowolnym momencie.
 * Lekarz może zmienić swój status samodzielnie lub za pośrednictwem administratora.
 * Lekarz może zmienić swój status na dostępny, jeśli nie ma żadnych przeszkód w jego pracy.
 * Lekarz może zmienić swój status na niedostępny, jeśli ma przeszkody w swojej pracy.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest na urlopie.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest chory.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest zajęty innymi obowiązkami.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza miejscem pracy.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem sieci.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem internetu.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem telefonu.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem komputera.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem smartfona.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem tabletu.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem laptopa.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem stacjonarnego komputera.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem stacjonarnego telefonu.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem stacjonarnego smartfona.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem stacjonarnego tabletu.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem stacjonarnego laptopa.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem stacjonarnego komputera.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem stacjonarnego telefonu.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem stacjonarnego smartfona.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem stacjonarnego tabletu.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem stacjonarnego laptopa.
 * Lekarz może zmienić swój status na niedostępny, jeśli jest poza zasięgiem stacjonarnego komputera.
 */
public enum DoctorStatus {
    AVAILABLE,
    UNAVAILABLE
}
