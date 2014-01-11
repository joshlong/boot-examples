package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;

/**
 * The {@link #main(String[])} method is an entry point and can be used to start the entire application, including
 * an embedded Tomcat instance.
 * <p/>
 * However, this class is also a {@link org.springframework.boot.web.SpringBootServletInitializer SpringBootServletInitializer}
 * subclass and so can be deployed into a traditional Servlet 3 container (Apache Tomcat 7, Jetty 9, JBoss AS 6, etc.)
 * and run from there, as well.
 */
@Configuration
@EnableSpringDataWebSupport
@EnableTransactionManagement
@ComponentScan
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {

    private static Class<Application> entryPointClass = Application.class;

    public static void main(String[] args) {
        SpringApplication.run(entryPointClass, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(entryPointClass);
    }
}

/**
 * Represents one (very under-specified) <em>booking</em>
 * for a named person at a theoretical establishment like a restaurant.
 */
@Entity
class Booking {

    @Column(name = "booking_name")
    private String bookingName;

    @Id
    @GeneratedValue
    @Column(name = "booking_id")
    private long id;

    public String getBookingName() {
        return bookingName;
    }

    public void setBookingName(String bookingName) {
        this.bookingName = bookingName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Booking() {
    }

    public Booking(String bookingName) {
        this.bookingName = bookingName;
    }
}

/**
 * Spring Data JPA-powered <em>repository</em> interface.
 * Supports common operations like {@link #findAll()} and {@link #save(Object)} against JPA entities.
 * This particular repository deals in {@link demo.Booking booking} objects.
 */
interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findByBookingName(@Param("bookingName") String bookingName);
}

/**
 * Handles REST-API calls for {@link demo.Booking booking data}.
 */
@RestController
@RequestMapping("/bookings")
class BookingRestController {

    @Autowired
    BookingRepository bookingRepository;

    @RequestMapping(method = RequestMethod.POST)
    Booking add(@RequestBody Booking b) {
        return this.bookingRepository.save(b);
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Booking> all() {
        return this.bookingRepository.findAll();
    }
}

/**
 * Handles the Thymeleaf-powered view responses.
 */
@Controller
class BookingHtmlController {

    @Autowired
    BookingRepository bookingRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/bookings.html")
    String all(Model model) {
        model.addAttribute("bookings", this.bookingRepository.findAll());
        return "bookings";
    }
}
