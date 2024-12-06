package nl.oudhoff.bastephenking.util;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class InsertBookcover {

    private final BookService bookService;
    private final BookcoverRepository bookcoverRepository;

    public InsertBookcover(BookService bookService, BookcoverRepository bookcoverRepository) {
        this.bookService = bookService;
        this.bookcoverRepository = bookcoverRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() throws IOException {
        Path folderPath = Paths.get("uploads");

        if (Files.exists(folderPath)) {
            List<String> bookcoverFiles = new ArrayList<>();

            // Iterate through the files in the uploads/bookcover directory
            Files.list(folderPath).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String filename = filePath.getFileName().toString();
                    bookcoverFiles.add(filename);
                }
            });


            for (String bookcoverFile : bookcoverFiles) {
                List<BookOutputDto> books = bookService.getAllBooks();
                String bookname = bookcoverFile.replace(".jpg", "");

                // Save bookcover in the repository
                bookcoverRepository.save(new Bookcover(bookcoverFile));

                // Add bookcover to the corresponding book
                for (BookOutputDto book : books) {
                    if (book.getTitle().equalsIgnoreCase(bookname)) {
                        bookService.addBookcoverToBook(bookcoverFile, book.getId());
                    }
                }
            }

        } else {
            System.out.println("Uploads folder does not exist: " + folderPath.toAbsolutePath());
        }
    }
}
