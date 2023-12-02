/* 

* Code inspired/taken/learned from:
- https://www.geeksforgeeks.org/operating-system-dining-philosopher-problem-using-semaphores/
- https://pseudomuto.com/2014/03/dining-philosophers-in-c/
- https://docs.oracle.com/cd/E19205-01/820-0619/gepji/index.html

Each Philosopher P

while true do {
    Think(P[i]);
    Pickup(P[i], Chopstick[i], Chopstick[(i + 1) mod 5]);
    Eat(P[i]);
    Putdown(P[i], Chopstick[i], Chopstick[(i + 1) mod 5]);
}

*/

#include <pthread.h>
#include <semaphore.h>
#include <stdio.h>
#include <stdlib.h>

typedef struct {
    int positionOfP;
    int nP;
    sem_t *lock;
    sem_t *forks;
} state_t;

void think(int positionOfP);
void eat(int positionOfP);
void *philosopher(void *state);
void initializeSemaphores(sem_t *lock, sem_t *forks, int nForks);
void runAllThreads(pthread_t *threads, sem_t *forks, sem_t *lock, int nP);

int main(int argc, char *args[]) {
    int nP = 500;

    sem_t lock;
    sem_t forks[nP];

    pthread_t P[nP];

    initializeSemaphores(&lock, forks, nP);
    runAllThreads(P, forks, &lock, nP);

    pthread_exit(NULL); /* Waits for threads to be completed */

    printf("All threads completed");
}

void think(int positionOfP) {
    printf("Philosopher %d thinks\n", positionOfP);
}
void eat(int positionOfP) {
    printf("Philosopher %d eats\n", positionOfP);
}

void *philosopher(void *state) /*called from each thread */
{
  int i;
  state_t self = *(state_t *)state;

  for(i = 0; i < 3; i++) {
    think(self.positionOfP);

    /* wait to unlock, to be sure that max N-1 philosophers will be eating at the same time */
    sem_wait(self.lock);

    /* take two forks, these semaphores are initialized to 1,
     meaning only one one person can pick it up */
    sem_wait(&self.forks[self.positionOfP]);
    sem_wait(&self.forks[(self.positionOfP + 1) % self.nP]);

    eat(self.positionOfP);

    /* put down forks */
    sem_post(&self.forks[self.positionOfP]);
    sem_post(&self.forks[(self.positionOfP + 1) % self.nP]);

    /* unlock lock*/
    sem_post(self.lock);
  }

  think(self.positionOfP);

  pthread_exit(NULL);
}

void runAllThreads(pthread_t *threads, sem_t *forks, sem_t *lock, int nP)
{
  int i;
  for(i = 0; i < nP; i++) {
    state_t *arg = malloc(sizeof(state_t));
    arg->positionOfP = i;
    arg->nP = nP;
    arg->lock = lock;
    arg->forks = forks;

    pthread_create(&threads[i], NULL, philosopher, (void *)arg);
  }
}

void initializeSemaphores(sem_t *lock, sem_t *forks, int nForks) {
    int i;
    for(i = 0; i < nForks; i++) {
        sem_init(&forks[i], 0, 1);
    }

    /* Locking forks, but minus one fork to avoid deadlock */
    sem_init(lock, 0, nForks - 1);
}