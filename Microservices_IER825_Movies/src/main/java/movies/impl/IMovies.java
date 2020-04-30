package movies.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import movies.Movies.Movie;
import movies.Movies.MovieId;
import movies.Movies.MovieIdList;
import movies.Movies.MovieList;

@Path("movies")
@Consumes({"application/x-protobuf", "application/json"})
@Produces({"application/x-protobuf", "application/json"})
public interface IMovies {
	
	@GET
	@Path("/")
	public MovieList moviesListGet();
	
	@GET
	@Path("/{id}")
	public Movie movieGet(@PathParam("id") int id);
	
	@POST
	@Path("/")
	public MovieId moviePost(Movie movie);
	
	@PUT
	@Path("/{id}")
	public void moviePut(@PathParam("id") int id, Movie movie);
	
	@DELETE
	@Path("/{id}")
	public void movieDelete(@PathParam("id") int id);
	
	@GET
	@Path("/find")
	public MovieIdList find(@QueryParam("year") int year, @QueryParam("orderby") String field);
}
