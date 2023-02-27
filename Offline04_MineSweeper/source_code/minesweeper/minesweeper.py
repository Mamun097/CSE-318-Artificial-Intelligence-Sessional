import itertools
import random


class Minesweeper():
    """
    Minesweeper game representation
    """

    def __init__(self, height=8, width=8, mines=8):

        # Set initial width, height, and number of mines
        self.height = height
        self.width = width
        self.mines = set()

        # Initialize an empty field with no mines
        self.board = []
        for i in range(self.height):
            row = []
            for j in range(self.width):
                row.append(False)
            self.board.append(row)

        # Add mines randomly
        while len(self.mines) != mines:
            i = random.randrange(height)
            j = random.randrange(width)
            if not self.board[i][j]:
                self.mines.add((i, j))
                self.board[i][j] = True

        # At first, player has found no mines
        self.mines_found = set()

    def print(self):
        """
        Prints a text-based representation
        of where mines are located.
        """
        for i in range(self.height):
            print("--" * self.width + "-")
            for j in range(self.width):
                if self.board[i][j]:
                    print("|X", end="")
                else:
                    print("| ", end="")
            print("|")
        print("--" * self.width + "-")

    def is_mine(self, cell):
        i, j = cell
        return self.board[i][j]

    def nearby_mines(self, cell):
        """
        Returns the number of mines that are
        within one row and column of a given cell,
        not including the cell itself.
        """

        # Keep count of nearby mines
        count = 0

        r,c=cell

        #checking only right,left,top and bottom cell
        if c > 0 and self.board[r][c-1]:
            count += 1
        if c < self.width - 1 and self.board[r][c+1]:
            count += 1

        if r > 0 and self.board[r-1][c]:
            count += 1
        if r < self.height - 1 and self.board[r+1][c]:
            count += 1

        return count

    def won(self):
        """
        Checks if all mines have been flagged.
        """
        return self.mines_found == self.mines


class Sentence():
    """
    Logical statement about a Minesweeper game
    A sentence consists of a set of board cells,
    and a count of the number of those cells which are mines.
    """

    def __init__(self, cells, count):
        self.cells = set(cells)
        self.count = count

    def __eq__(self, other):
        return self.cells == other.cells and self.count == other.count

    def __str__(self):
        return f"{self.cells} = {self.count}"

    def known_mines(self):
        """
        Returns the set of all cells in self.cells known to be mines.
        """
        if len(self.cells) == self.count:
            return self.cells
        else:
            return None

    def known_safes(self):
        """
        Returns the set of all cells in self.cells known to be safe.
        """
        if self.count == 0:
            return self.cells
        else:
            return None

    def mark_mine(self, cell):
        """
        Updates internal knowledge representation given the fact that
        a cell is known to be a mine.
        """
        if cell not in self.cells:
            return

        self.cells.remove(cell)
        self.count -= 1

    def mark_safe(self, cell):
        """
        Updates internal knowledge representation given the fact that
        a cell is known to be safe.
        """
        if cell not in self.cells:
            return

        self.cells.remove(cell)


class MinesweeperAI():
    """
    Minesweeper game player
    """

    def __init__(self, height=8, width=8):

        # Set initial height and width
        self.height = height
        self.width = width

        # Keep track of which cells have been clicked on
        self.moves_made = set()

        # Keep track of cells known to be safe or mines
        self.mines = set()
        self.safes = set()

        # List of sentences about the game known to be true
        self.knowledge = []

    def mark_mine(self, cell):
        """
        Marks a cell as a mine, and updates all knowledge
        to mark that cell as a mine as well.
        """
        self.mines.add(cell)
        for sentence in self.knowledge:
            sentence.mark_mine(cell)

    def mark_safe(self, cell):
        """
        Marks a cell as safe, and updates all knowledge
        to mark that cell as safe as well.
        """
        self.safes.add(cell)
        for sentence in self.knowledge:
            sentence.mark_safe(cell)

    def add_knowledge(self, cell, count):
        """
        Called when the Minesweeper board tells us, for a given
        safe cell, how many neighboring cells have mines in them.

        This function should:
            1) mark the cell as a move that has been made
            2) mark the cell as safe
            3) add a new sentence to the AI's knowledge base
               based on the value of `cell` and `count`
            4) mark any additional cells as safe or as mines
               if it can be concluded based on the AI's knowledge base
            5) add any new sentences to the AI's knowledge base
               if they can be inferred from existing knowledge
        """
        # 1
        self.moves_made.add(cell)
        # 2
        self.mark_safe(cell)
        # 3
        cells = set()
        i, j = cell
        neighbors = [(i-1, j), (i+1, j), (i, j-1), (i, j+1)]

        for nbr in neighbors:
            x, y = nbr
            if x < 0 or x >= self.height or y < 0 or y >= self.width:
                continue
            if nbr not in self.moves_made and nbr not in self.mines:
                cells.add(nbr)
            elif nbr in self.mines:
                count -= 1
        self.knowledge.append(Sentence(cells, count))

        # 4
        for sentence in self.knowledge:
            if sentence.known_mines():
                for mine in sentence.known_mines().copy():
                    self.mark_mine(mine)
            if sentence.known_safes():
                for safe in sentence.known_safes().copy():
                    self.mark_safe(safe)
        # 5
        for s1 in self.knowledge:
            for s2 in self.knowledge:
                if s1 is s2:
                    continue
                if s1 == s2:
                    self.knowledge.remove(s2)
                    continue
                if s1.cells.issubset(s2.cells):
                    s3 = Sentence(s2.cells - s1.cells, s2.count - s1.count)
                    if s3 not in self.knowledge:
                        self.knowledge.append(s3)

    def make_safe_move(self):
        """
        Returns a safe cell to choose on the Minesweeper board.
        The move must be known to be safe, and not already a move
        that has been made.

        This function may use the knowledge in self.mines, self.safes
        and self.moves_made, but should not modify any of those values.
        """
        for s in self.safes:
            if s not in self.moves_made:
                return s
        return None

    def make_random_move(self):
        """
        Returns a move to make on the Minesweeper board.
        Should choose randomly among cells that:
            1) have not already been chosen, and
            2) are not known to be mines
        """
        m = random.randrange(self.height)
        n = random.randrange(self.width)
        while (m, n) in self.moves_made or (m, n) in self.mines:
            m = random.randrange(self.height)
            n = random.randrange(self.width)
        return m, n
