package edu.cit.rivero.workspace.features.space;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;

    public SpaceService(SpaceRepository spaceRepository, AvailabilitySlotRepository availabilitySlotRepository) {
        this.spaceRepository = spaceRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
    }

    public Space createSpace(Space space) {
        if (space.getId() == null || space.getId().isBlank()) {
            space.setId(UUID.randomUUID().toString());
        }
        if (space.getRating() == null) {
            space.setRating(0.0);
        }
        if (space.getAvailable() == null) {
            space.setAvailable(true);
        }
        return spaceRepository.save(space);
    }

    public Space updateSpace(String id, Space updatedSpace) {
        Space existing = spaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + id));

        existing.setName(updatedSpace.getName());
        existing.setLocation(updatedSpace.getLocation());
        existing.setType(updatedSpace.getType());
        existing.setCapacity(updatedSpace.getCapacity());
        existing.setHourlyRate(updatedSpace.getHourlyRate());
        existing.setDescription(updatedSpace.getDescription());
        existing.setImageUrl(updatedSpace.getImageUrl());
        existing.setAmenities(updatedSpace.getAmenities());
        existing.setUtilities(updatedSpace.getUtilities());
        existing.setCheckInWindow(updatedSpace.getCheckInWindow());
        existing.setCancellationPolicy(updatedSpace.getCancellationPolicy());

        if (updatedSpace.getRating() != null) {
            existing.setRating(updatedSpace.getRating());
        }
        if (updatedSpace.getAvailable() != null) {
            existing.setAvailable(updatedSpace.getAvailable());
        }

        return spaceRepository.save(existing);
    }

    public void deleteSpace(String id) {
        if (!spaceRepository.existsById(id)) {
            throw new RuntimeException("Workspace not found with id: " + id);
        }
        spaceRepository.deleteById(id);
    }

    @Transactional
    public List<AvailabilitySlot> replaceAvailability(String spaceId, List<AvailabilitySlot> slots) {
        availabilitySlotRepository.deleteBySpaceId(spaceId);
        availabilitySlotRepository.flush();
        slots.forEach(s -> { s.setId(null); s.setSpaceId(spaceId); });
        return availabilitySlotRepository.saveAll(slots);
    }

    public Space uploadImage(String id, MultipartFile[] files) {
        Space existing = spaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + id));

        try {
            Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            StringBuilder urls = new StringBuilder();
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".") 
                        ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                        : ".jpg";
                String newFilename = UUID.randomUUID().toString() + extension;

                Path filePath = uploadPath.resolve(newFilename);
                Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                if (urls.length() > 0) urls.append(",");
                urls.append("/uploads/").append(newFilename);
            }

            if (existing.getImageUrl() != null && !existing.getImageUrl().trim().isEmpty()) {
                urls.insert(0, existing.getImageUrl() + ",");
            }
            
            existing.setImageUrl(urls.toString().replaceAll(",$", ""));
            return spaceRepository.save(existing);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store files", e);
        }
    }

    public WeatherDto getWeatherForSpace(String spaceId) {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=10.3157&longitude=123.8854&current_weather=true";
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        try {
            java.util.Map<String, Object> response = restTemplate.getForObject(url, java.util.Map.class);
            if (response != null && response.containsKey("current_weather")) {
                java.util.Map<String, Object> currentWeather = (java.util.Map<String, Object>) response.get("current_weather");
                WeatherDto dto = new WeatherDto();
                if (currentWeather.get("temperature") instanceof Number) {
                    dto.setTemperature(((Number) currentWeather.get("temperature")).doubleValue());
                }
                if (currentWeather.get("weathercode") instanceof Number) {
                    dto.setWeatherCode(((Number) currentWeather.get("weathercode")).intValue());
                }
                return dto;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new WeatherDto();
    }

    public CoordinatesDto getCoordinatesForSpace(String spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + spaceId));
        
        String location = space.getLocation();
        if (location == null || location.isBlank()) {
            return new CoordinatesDto(10.3157, 123.8854); // Cebu fallback
        }

        try {
            String encodedLocation = java.net.URLEncoder.encode(location, "UTF-8");
            String url = "https://nominatim.openstreetmap.org/search?q=" + encodedLocation + "&format=json&limit=1";
            
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "WorkSpaceApp/1.0");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
            
            org.springframework.http.ResponseEntity<java.util.List> response = restTemplate.exchange(
                    url, org.springframework.http.HttpMethod.GET, entity, java.util.List.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
                java.util.Map<String, Object> firstResult = (java.util.Map<String, Object>) response.getBody().get(0);
                Double lat = Double.parseDouble(firstResult.get("lat").toString());
                Double lon = Double.parseDouble(firstResult.get("lon").toString());
                return new CoordinatesDto(lat, lon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new CoordinatesDto(10.3157, 123.8854); // Cebu fallback
    }
}

