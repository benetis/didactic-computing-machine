package test

import (
	"database/sql"
	"github.com/leanovate/gopter"
	gc "github.com/leanovate/gopter/convey"
	_ "github.com/mattn/go-sqlite3"
	. "github.com/smartystreets/goconvey/convey"
	"property/model"
	"property/repository"
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
			if err := repo.Save(ticket); err != nil {
				t.Logf("Failed to save: %v", err)
				return false
			}

			retrieved, err := repo.GetByID(ticket.ID)
			if err != nil {
				t.Logf("Failed to retrieve: %v", err)
				return false
			}

			return *retrieved == ticket
		}, gc.ShouldSucceedForAll, genTicket)
	})
}
