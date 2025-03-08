package test

import (
	"database/sql"
	"github.com/leanovate/gopter"
	gc "github.com/leanovate/gopter/convey"
	_ "github.com/mattn/go-sqlite3"
	. "github.com/smartystreets/goconvey/convey"
	"property/model"
	"property/repository"
	"property/test/factory"
	"testing"
)

func TestTicketIntegration(t *testing.T) {
	db, err := sql.Open("sqlite3", ":memory:")
	if err != nil {
		t.Fatal(err)
	}
	defer db.Close()

	repo := repository.NewTicketRepo(db)
	if err := repo.Init(); err != nil {
		t.Fatal(err)
	}

	parameters := gopter.DefaultTestParameters()
	parameters.MinSuccessfulTests = 20 // Run at least 20 tests
	genTicket := TicketGenerator()

	Convey("Ticket database integration tests", t, func() {
		So(func(ticket model.Ticket) bool {
			err := repo.Save(ticket)
			So(err, ShouldBeNil)

			retrieved, err := repo.GetByID(ticket.ID)
			So(err, ShouldBeNil)

			return *retrieved == ticket
		}, gc.ShouldSucceedForAll, genTicket)
	})

	Convey("With factory instead of generator", t, func() {
		ticket := factory.NewTicket()

		err := repo.Save(ticket)
		So(err, ShouldBeNil)

		retrieved, err := repo.GetByID(ticket.ID)
		So(err, ShouldBeNil)

		So(*retrieved, ShouldResemble, ticket)
	})
}
