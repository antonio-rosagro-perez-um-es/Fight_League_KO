package character.dto;

public record CharacterUpdateDTO(
        String name,
        String description,
        String region,
        String archetype,
        String title,
        String itLikes,
        String itDislike,
        String slug,
        Integer health,
        Integer range,
        Integer power,
        Integer vitality,
        Integer mobility,
        Integer easyOfUse) {
}