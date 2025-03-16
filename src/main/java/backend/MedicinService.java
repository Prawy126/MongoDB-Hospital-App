package backend;

import org.bson.types.ObjectId;
import java.util.Date;

public class MedicinService {
    private final PrescriptionRepository prescriptionRepo;
    private final MedicineAdministrationRepository administrationRepo;

    public void administerMedicine(ObjectId prescriptionId, ObjectId nurseId, String administeredDose) {
        Prescription prescription = prescriptionRepo.findById(prescriptionId);

        // Check if the prescription is active
        if (!"active".equals(prescription.getStatus())) {
            throw new RuntimeException("Prescription is inactive!");
        }

        // Check dose compliance
        if (!prescription.getDosage().contains(administeredDose)) {
            throw new RuntimeException("Invalid dose!");
        }

        // Save medicine administration
        MedicineAdministration administration = new MedicineAdministration(
                prescriptionId,
                nurseId,
                new Date(),
                administeredDose,
                "Standard administration"
        );
        administrationRepo.saveAdministration(administration);
    }
}