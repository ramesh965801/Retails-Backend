package com.retail.RM_RETAIL.controller;

import com.retail.RM_RETAIL.entity.Item;
import com.retail.RM_RETAIL.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:3000")
public class ItemController {

    private final ItemService itemService;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // ✅ GET ALL ITEMS
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    // ✅ SAVE ITEM WITH IMAGE
    @PostMapping(consumes = {"multipart/form-data"})
    public Item saveItem(@RequestParam("name") String name,
                         @RequestParam("category") String category,
                         @RequestParam("price") Double price,
                         @RequestParam("designation") String designation,
                         @RequestParam("image") MultipartFile file) throws IOException {

        // Create upload folder if not exist
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Create File Name
        String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());

        // Save Image
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Generate Image URL
        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(fileName)
                .toUriString();

        // Set data to entity
        Item item = new Item();
        item.setName(name);
        item.setCategory(category);
        item.setPrice(price);
        item.setDesignation(designation);
        item.setImageUrl(imageUrl);

        return itemService.saveItem(item);
    }

    // ✅ DELETE ITEM
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
