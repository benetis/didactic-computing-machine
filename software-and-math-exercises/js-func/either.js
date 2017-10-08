
const Right = x => ({
  map: f => Right(f(x)),
  inspect: () => `Right<${x}>`,
  fold: (f, g) => g(x)
});

const Left = x => ({
  map: f => Left(x),
  inspect: () => `Left<${x}>`,
  fold: (f, g) => f(x)
});


const findName = id => {
  const nameFound = { 1: 'Zerg', 2: 'Batman'}[id];
  return nameFound ? Right(nameFound) : Left(null)
};

console.log(
  findName(1).map(name => name.toUpperCase()).fold(n => 'Not found', c => c.slice(0, 3)),
  findName(3).map(name => name.toUpperCase()).fold(n => 'Not found', c => c.slice(0, 3))
);
